package com.barcode.tes.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.StarHalf
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import com.barcode.tes.domain.model.Order
import com.barcode.tes.domain.model.Product
import com.barcode.tes.presentation.viewmodel.AuthViewModel
import com.barcode.tes.presentation.viewmodel.BannerViewModel
import com.barcode.tes.presentation.viewmodel.ProductViewModel
import com.barcode.tes.presentation.viewmodel.TransactionViewModel
import com.barcode.tes.ui.components.BottomNavigationBar
import com.barcode.tes.ui.navigation.Screen
import com.barcode.tes.domain.model.Profile
import com.barcode.tes.domain.model.Banner
import androidx.compose.ui.tooling.preview.Preview
import com.barcode.tes.ui.theme.TesTheme
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    productViewModel: ProductViewModel = viewModel(),
    transactionViewModel: TransactionViewModel = viewModel(),
    bannerViewModel: BannerViewModel = viewModel(),
    onThemeToggle: () -> Unit,
    isDarkTheme: Boolean
) {
    val userProfile by authViewModel.userProfile.collectAsState()
    val authError by authViewModel.error.collectAsState()
    val allProducts by productViewModel.products.collectAsState()
    val allBanners by bannerViewModel.banners.collectAsState()
    val orders by transactionViewModel.orders.collectAsState()
    val isMainLoading by authViewModel.isLoading.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val currentUser = authViewModel.currentUser
    
    // Sinkronisasi data saat masuk ke halaman Profile
    LaunchedEffect(currentUser) {
        productViewModel.fetchProducts()
        bannerViewModel.fetchBanners()
        currentUser?.id?.let { transactionViewModel.fetchOrders(it) }
    }

    ProfileScreenContent(
        userProfile = userProfile,
        authError = authError,
        allProducts = allProducts,
        allBanners = allBanners,
        orders = orders,
        isMainLoading = isMainLoading,
        isLoading = isLoading,
        currentUserId = currentUser?.id,
        isDarkTheme = isDarkTheme,
        onThemeToggle = onThemeToggle,
        onLogout = {
            authViewModel.logout {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        },
        onDeleteAccount = {
            authViewModel.deleteAccount {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        },
        onUpdateProfile = { updatedProfile ->
            authViewModel.updateProfile(updatedProfile) {
                // success
            }
        },
        onUploadAvatar = { bytes, onComplete ->
            authViewModel.uploadAvatar(bytes, onComplete)
        },
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(
    userProfile: Profile?,
    authError: String?,
    allProducts: List<Product>,
    allBanners: List<Banner>,
    orders: List<Order>,
    isMainLoading: Boolean,
    isLoading: Boolean,
    currentUserId: String?,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onUpdateProfile: (Profile) -> Unit,
    onUploadAvatar: (ByteArray, (String) -> Unit) -> Unit,
    navController: NavController
) {
    // Filter produk milik user ini saja untuk statistik seller
    val myProducts = remember(allProducts, currentUserId) {
        allProducts.filter { it.user_id == currentUserId }
    }

    // Filter iklan (banner) milik user ini saja
    val myBanners = remember(allBanners, currentUserId) {
        allBanners.filter { it.user_id == currentUserId }
    }

    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogContent by remember { mutableStateOf<@Composable () -> Unit>({}) }

    // State untuk Konfirmasi Logout/Hapus
    var showConfirmDialog by remember { mutableStateOf(false) }
    var confirmType by remember { mutableStateOf("") } // "logout" atau "delete"

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editShopName by remember { mutableStateOf("") }
    var editAvatarUrl by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val bytes = context.contentResolver.openInputStream(it)?.use { stream -> stream.readBytes() }
                    bytes?.let { b ->
                        onUploadAvatar(b) { url ->
                            // Tambahkan timestamp agar Coil mendeteksi perubahan gambar
                            editAvatarUrl = "$url?t=${System.currentTimeMillis()}"
                        }
                    }
                } catch (e: Exception) {
                    // Error ditangani oleh AuthViewModel (_error)
                }
            }
        }
    }

    LaunchedEffect(userProfile) {
        userProfile?.let {
            editName = it.full_name ?: ""
            editShopName = it.shop_name ?: ""
            editAvatarUrl = it.avatar_url ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BISNIZKU", fontWeight = FontWeight.Bold) },
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
            BottomNavigationBar(navController, currentRoute = Screen.Profile.route)
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Card
                ProfileInfoCard(
                    name = userProfile?.full_name ?: "Loading...",
                    shopName = userProfile?.shop_name ?: userProfile?.email ?: "Nama Toko belum diatur",
                    avatarUrl = userProfile?.avatar_url,
                    onEditClick = { showEditProfileDialog = true }
                )

                if (authError != null) {
                    Text(
                        text = authError,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Chart Section
                ChartCard(orders)

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox(
                        label = "Produk Laku", 
                        value = myProducts.count { it.status == "sold" }.toString(), 
                        color = Color(0xFF90EE90), 
                        modifier = Modifier.weight(1f),
                        onClick = {
                            dialogTitle = "Daftar Produk Laku"
                            dialogContent = {
                                ProductListDialogContent(myProducts.filter { it.status == "sold" })
                            }
                            showDialog = true
                        }
                    )
                    StatBox(
                        label = "Produk Tidak Laku", 
                        value = myProducts.count { it.status == "inactive" }.toString(), 
                        color = Color(0xFFADD8E6), 
                        modifier = Modifier.weight(1f),
                        onClick = {
                            dialogTitle = "Produk Tidak Laku"
                            dialogContent = {
                                ProductListDialogContent(myProducts.filter { it.status == "inactive" })
                            }
                            showDialog = true
                        }
                    )

                    // Hitung Laba Bersih yang Akurat (Harga Jual dikurang Harga Modal)
                    val mySales = orders.filter { it.seller_id == currentUserId && it.payment_status == "paid" }
                    val netProfit = mySales.sumOf { (it.grand_total ?: 0.0) - (it.capital_price ?: 0.0) }
                    val formattedNetProfit = remember(netProfit) {
                        java.text.DecimalFormat("#,###").format(netProfit)
                    }

                    StatBox(
                        label = "Laba Bersih", 
                        value = "Rp $formattedNetProfit", 
                        color = Color(0xFFF08080), 
                        modifier = Modifier.weight(1.2f), // Sedikit lebih lebar untuk angka Rupiah
                        onClick = {
                            navController.navigate(Screen.SalesHistory.route)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Kotak: Produk Saya
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .clickable {
                            navController.navigate(Screen.MyProducts.route)
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Inventory2, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Produk Saya", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        }
                        Text("${myProducts.size} Produk", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    }
                }


                Spacer(modifier = Modifier.height(12.dp))

                //BANNER
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .clickable {
                            navController.navigate(Screen.MyBanners.route)
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Iklan Saya", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        }
                        Text("${myBanners.size} Iklan", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    }
                }



                Spacer(modifier = Modifier.height(24.dp))

                // Settings List
                SettingsItem("Email & Password", Icons.Default.ChevronRight)
                SettingsItem("Privacy & Policy", Icons.Default.ChevronRight)
                SettingsItem("Metode Pembayaran", Icons.Default.ChevronRight)
                SettingsItem("Iklan", Icons.Default.ChevronRight)
                SettingsItem("Pusat bantuan", Icons.Default.ChevronRight)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Bottom Settings
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                SettingsItem("Privasi", null)
                SettingsItem("Keamanan", null)
                SettingsItem("Hapus Akun", null, textColor = Color.Red, onClick = {
                    confirmType = "delete"
                    showConfirmDialog = true
                })
                SettingsItem("Logout", null, textColor = Color.Red, onClick = {
                    confirmType = "logout"
                    showConfirmDialog = true
                })
            }

            if (isMainLoading) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    // Dialog Konfirmasi (Expert Level UI Feedback)
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(if (confirmType == "logout") "Logout" else "Hapus Akun") },
            text = { 
                Text(
                    if (confirmType == "logout") "Apakah Anda yakin ingin keluar dari aplikasi?" 
                    else "Tindakan ini permanen. Semua data produk dan profil Anda akan dihapus. Yakin?"
                ) 
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        if (confirmType == "logout") {
                            onLogout()
                        } else {
                            onDeleteAccount()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (confirmType == "logout") MaterialTheme.colorScheme.primary else Color.Red
                    )
                ) {
                    Text("Ya, Lanjutkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(dialogTitle) },
            text = { dialogContent() },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { if (!isLoading) showEditProfileDialog = false },
            title = { Text("Edit Profil") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            .clickable(enabled = !isLoading) { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (editAvatarUrl.isNotBlank()) {
                            AsyncImage(
                                model = editAvatarUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(32.dp))
                        }
                        
                        if (isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(30.dp))
                            }
                        }
                    }
                    Text(
                        text = if (isLoading) "Sedang mengunggah..." else "Klik foto untuk mengubah", 
                        fontSize = 12.sp, 
                        color = if (isLoading) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nama Lengkap") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editShopName,
                        onValueChange = { editShopName = it },
                        label = { Text("Nama Toko") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        userProfile?.let {
                            val updatedProfile = it.copy(
                                full_name = editName,
                                shop_name = editShopName,
                                avatar_url = editAvatarUrl
                            )
                            onUpdateProfile(updatedProfile)
                            showEditProfileDialog = false
                        }
                    },
                    enabled = !isLoading // Kunci tombol saat sedang upload
                ) {
                    if (isLoading) {
                        Text("Tunggu...")
                    } else {
                        Text("Simpan")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}


@Composable
fun ProfileInfoCard(name: String, shopName: String, avatarUrl: String? = null, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditClick() }
            .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (!avatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Person, 
                        contentDescription = null, 
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name.uppercase(), 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 18.sp, 
                    color = MaterialTheme.colorScheme.primary
                )
                Text(text = shopName, fontSize = 14.sp, color = Color.Gray)
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    repeat(4) { Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp)) }
                    Icon(Icons.AutoMirrored.Outlined.StarHalf, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                }
            }
            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun ProductListDialogContent(products: List<Product>) {
    if (products.isEmpty()) {
        Text("Tidak ada produk.")
    } else {
        Column(modifier = Modifier.heightIn(max = 400.dp).verticalScroll(rememberScrollState())) {
            products.forEach { product ->
                ListItem(
                    headlineContent = { Text(product.name) },
                    supportingContent = { Text("Harga: Rp ${product.price} | Stok: ${product.stock} ${product.unit}") },
                    trailingContent = {
                        Text(product.status.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (product.status == "active") Color(0xFF4CAF50) else Color.Red)
                    }
                )
                HorizontalDivider(modifier = Modifier.alpha(0.3f))
            }
        }
    }
}

@Composable
fun LabaContent(orders: List<Order>) {
    val totalRevenue = orders.filter { it.payment_status == "paid" }.sumOf { it.total_amount ?: 0.0 }
    val totalCapital = orders.filter { it.payment_status == "paid" }.sumOf { it.capital_price ?: 0.0 }
    val labaBersih = totalRevenue - totalCapital

    Column {
        Text("Total Omzet: Rp ${java.text.DecimalFormat("#,###").format(totalRevenue)}", fontWeight = FontWeight.Bold)
        Text("Total Modal: Rp ${java.text.DecimalFormat("#,###").format(totalCapital)}")
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Text("Laba Bersih: Rp ${java.text.DecimalFormat("#,###").format(labaBersih)}", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ChartCard(orders: List<Order>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).background(Color(0xFFFFA07A)))
                Text(" Penjualan", fontSize = 12.sp, modifier = Modifier.padding(end = 8.dp), color = MaterialTheme.colorScheme.onSurface)
                Box(modifier = Modifier.size(10.dp).background(Color(0xFF6495ED)))
                Text(" Volume", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            if (orders.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    Text("Belum ada data penjualan", fontSize = 12.sp, color = Color.Gray)
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val displayData = orders.takeLast(7)
                    displayData.forEachIndexed { index, order ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                val height1 = ((order.total_amount ?: 0.0) / 1000).toInt().coerceIn(10, 100)
                                val height2 = ((order.grand_total ?: 0.0) / 1000).toInt().coerceIn(10, 100)
                                Box(modifier = Modifier.width(12.dp).height(height1.dp).background(Color(0xFF6495ED)))
                                Box(modifier = Modifier.width(12.dp).height(height2.dp).background(Color(0xFFFFA07A)))
                            }
                            Text("D${index + 1}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color.Black, maxLines = 1)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun SettingsItem(
    title: String, 
    icon: ImageVector?, 
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    onClick: (() -> Unit)? = null
) {
    Column(modifier = Modifier.clickable(enabled = onClick != null) { onClick?.invoke() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textColor)
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }
        }
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    TesTheme {
        ProfileScreenContent(
            userProfile = Profile(
                id = "user123",
                full_name = "John Doe",
                shop_name = "John's Shop",
                email = "john@example.com",
                avatar_url = null
            ),
            authError = null,
            allProducts = listOf(
                Product(id = "p1", user_id = "user123", name = "Product 1", price = 10000.0, status = "sold"),
                Product(id = "p2", user_id = "user123", name = "Product 2", price = 20000.0, status = "active"),
                Product(id = "p3", user_id = "user123", name = "Product 3", price = 30000.0, status = "inactive")
            ),
            allBanners = listOf(
                Banner(id = "b1", user_id = "user123", image_url = "")
            ),
            orders = listOf(
                Order(id = "o1", buyer_id = "b1", seller_id = "user123", total_amount = 15000.0, capital_price = 10000.0, grand_total = 15000.0, payment_status = "paid")
            ),
            isMainLoading = false,
            isLoading = false,
            currentUserId = "user123",
            isDarkTheme = false,
            onThemeToggle = {},
            onLogout = {},
            onDeleteAccount = {},
            onUpdateProfile = {},
            onUploadAvatar = { _, _ -> },
            navController = rememberNavController()
        )
    }
}
