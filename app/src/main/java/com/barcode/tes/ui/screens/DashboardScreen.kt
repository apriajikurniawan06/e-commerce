package com.barcode.tes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.barcode.tes.data.remote.SupabaseClient
import com.barcode.tes.domain.model.Banner
import com.barcode.tes.presentation.viewmodel.BannerViewModel
import com.barcode.tes.ui.components.BottomNavigationBar
import com.barcode.tes.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    onThemeToggle: () -> Unit,
    isDarkTheme: Boolean,
    viewModel: BannerViewModel = viewModel()
) {
    val allBanners by viewModel.banners.collectAsState()
    
    DashboardContent(
        navController = navController,
        onThemeToggle = onThemeToggle,
        isDarkTheme = isDarkTheme,
        allBanners = allBanners
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    navController: NavController,
    onThemeToggle: () -> Unit,
    isDarkTheme: Boolean,
    allBanners: List<Banner>
) {
    val banners = remember(allBanners) { allBanners.filter { it.is_active } }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("BISNIZKU", fontWeight = FontWeight.Bold)
                },
                actions = {
                    IconButton(onClick = onThemeToggle) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }

                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                    }

                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Help, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController, currentRoute = Screen.Dashboard.route)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Dynamic Banner Section
            if (banners.isNotEmpty()) {
                val bannerList = banners
                val pagerState = rememberPagerState(pageCount = { bannerList.size })
                
                // Auto-slide logic
                LaunchedEffect(pagerState.currentPage) {
                    kotlinx.coroutines.delay(5000L)
                    if (bannerList.isNotEmpty()) {
                        val nextPage = (pagerState.currentPage + 1) % bannerList.size
                        pagerState.animateScrollToPage(nextPage)
                    }
                }
                
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentPadding = PaddingValues(0.dp), // Setel ke 0 agar tidak ada gambar tetangga yang mengintip
                        pageSpacing = 0.dp // Tidak ada jarak antar halaman agar transisi bersih
                    ) { page ->
                        val banner = bannerList[page]
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp) // Padding di dalam halaman agar kartu tidak menyentuh pinggir layar
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = SupabaseClient.bannerImageUrl(banner.image_url),
                                    contentDescription = banner.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                // Store Name Overlay
                                banner.title?.let { title ->
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.6f),
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            text = title,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Pager Indicator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(bannerList.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(if (pagerState.currentPage == iteration) 8.dp else 6.dp)
                            )
                        }
                    }
                }
            } else {
                // Fallback dummy banner if no banners uploaded
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1460925895917-afdab827c52f?q=80&w=2426&auto=format&fit=crop",
                        contentDescription = "Banner Default",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Grid Menus
            val menus = listOf(
                MenuDataItem("Pulsa", Icons.Default.PhoneAndroid),
                MenuDataItem("PDAM", Icons.Default.WaterDrop),
                MenuDataItem("PLN", Icons.Default.ElectricBolt),
//                MenuDataItem("Game", Icons.Default.Games),
//                MenuDataItem("Zakat", Icons.Default.CleanHands),
                MenuDataItem("Multifinance", Icons.Default.AccountBalanceWallet),
                MenuDataItem("BPJS", Icons.Default.AccountBalance),
//                MenuDataItem("TV Kabel", Icons.Default.Tv),
//                MenuDataItem("Logistik", Icons.Default.LocalShipping),
                MenuDataItem("Tiket Pesawat", Icons.Default.Flight),
                MenuDataItem("Tiket Kapal", Icons.Default.DirectionsBoat),
                MenuDataItem("Tiket Kereta", Icons.Default.Train),
                MenuDataItem("Tiket Bus", Icons.Default.DirectionsBus),
                MenuDataItem("Hiburan", Icons.Default.Movie),
                MenuDataItem("Hotel", Icons.Default.Hotel),
                MenuDataItem("Voucher", Icons.Default.ConfirmationNumber),
                MenuDataItem("Kuliner", Icons.Default.Restaurant)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(menus) { menu ->
                    MenuItemCircle(menu) {
                        when (menu.title) {
                            "Pulsa" -> navController.navigate(Screen.Pulsa.route)
                            "PDAM" -> navController.navigate(Screen.Pdam.route)
                            "PLN" -> navController.navigate(Screen.Pln.route)
                            "Tiket Pesawat" -> navController.navigate(Screen.Flight.route)
                            "Tiket Kereta" -> navController.navigate(Screen.Train.route)
                            "Tiket Kapal" -> navController.navigate(Screen.Ship.route)
                            "Tiket Bus" -> navController.navigate(Screen.Bus.route)
                            else -> navController.navigate(Screen.Transaction.createRoute(menu.title))
                        }
                    }
                }
            }
        }
    }
}

data class MenuDataItem(val title: String, val icon: ImageVector)

@Composable
fun MenuItemCircle(menu: MenuDataItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(if (MaterialTheme.colorScheme.background == Color.Black) Color.DarkGray else Color(0xFFF0F0F0))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = menu.icon,
                contentDescription = menu.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = menu.title,
            fontSize = 10.sp,
            modifier = Modifier.padding(top = 4.dp),
            maxLines = 1,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


@Preview
@Composable
fun DashboardScreenPreview() {
    DashboardContent(
        navController = rememberNavController(),
        onThemeToggle = {},
        isDarkTheme = false,
        allBanners = emptyList()
    )
}
