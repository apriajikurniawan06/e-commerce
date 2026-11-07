package com.barcode.tes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.barcode.tes.domain.model.Product
import com.barcode.tes.presentation.viewmodel.ProductViewModel
import com.barcode.tes.ui.components.BottomNavigationBar
import com.barcode.tes.ui.navigation.Screen
import androidx.compose.ui.tooling.preview.Preview
import com.barcode.tes.domain.model.ProductCategory
import com.barcode.tes.ui.theme.TesTheme
import com.barcode.tes.data.remote.SupabaseClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    navController: NavController,
    viewModel: ProductViewModel = viewModel(),
    onThemeToggle: () -> Unit,
    isDarkTheme: Boolean
) {
    val products by viewModel.products.collectAsState()
    val categories by viewModel.categories.collectAsState()
    
    ProductScreenContent(
        products = products,
        categories = categories,
        isDarkTheme = isDarkTheme,
        onThemeToggle = onThemeToggle,
        onAddProductClick = { navController.navigate(Screen.AddProduct.route) },
        onProductClick = { product ->
            navController.navigate(Screen.ProductDetail.createRoute(product.id ?: ""))
        },
        bottomBar = {
            BottomNavigationBar(navController, currentRoute = Screen.ProductList.route)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreenContent(
    products: List<Product>,
    categories: List<ProductCategory>,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onAddProductClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    bottomBar: @Composable () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) } // null means "All"

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                TopAppBar(
                    title = { Text("BISNIZKU", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = onThemeToggle) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                                contentDescription = "Toggle Theme"
                            )
                        }
                        IconButton(onClick = {}) { Icon(Icons.Default.QrCodeScanner, null) }
                        IconButton(onClick = {}) { Icon(Icons.Default.Notifications, null) }
                    }
                )
                
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari produk...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = { if (searchQuery.isNotEmpty()) Icon(Icons.Default.Close, null, Modifier.clickable { searchQuery = "" }) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = if (isDarkTheme) Color.DarkGray else Color(0xFFF0F0F0),
                        unfocusedContainerColor = if (isDarkTheme) Color.DarkGray else Color(0xFFF0F0F0),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
                
                LazyRow(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryId == null,
                            onClick = { selectedCategoryId = null },
                            label = { Text("Semua") },
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategoryId == category.id,
                            onClick = { selectedCategoryId = category.id },
                            label = { Text(category.name) },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProductClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
                    Icon(Icons.Default.Storefront, null)
                    Text("Jualan", fontSize = 10.sp)
                }
            }
        },
        bottomBar = bottomBar
    ) { padding ->
        val filteredProducts = products.filter { product ->
            (selectedCategoryId == null || product.category_id == selectedCategoryId) &&
            product.name.contains(searchQuery, true)
        }

        if (filteredProducts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Tidak ada produk ditemukan")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(padding).padding(8.dp),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredProducts) { product ->
                    ProductCard(product) {
                        onProductClick(product)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                AsyncImage(
                    model = SupabaseClient.productImageUrl(product.thumbnail_url),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = android.R.drawable.ic_menu_gallery),
                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
                )
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    product.name,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // Format price to avoid scientific notation
                val formattedPrice = remember(product.price) {
                    java.text.DecimalFormat("#,###.##").format(product.price)
                }
                
                Text("Rp. $formattedPrice / ${product.unit}", color = Color.Gray, fontSize = 12.sp)
                if (product.stock <= 0) {
                    Text("Stok Habis", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text("Stok: ${product.stock}", color = Color(0xFF4CAF50), fontSize = 10.sp)
                }
            }
        }
    }
}

// Tambahkan import ini di bagian atas file jika diperlukan
// import androidx.compose.ui.res.painterResource

@Preview(showBackground = true)
@Composable
fun ProductScreenPreview() {
    val sampleCategories = listOf(
        ProductCategory(1, "Elektronik", null),
        ProductCategory(2, "Pakaian", null),
        ProductCategory(3, "Makanan", null)
    )

    val sampleProducts = listOf(
        Product(
            id = "1",
            user_id = "user1",
            category_id = 1,
            name = "Smartphone X",
            price = 5000000.0,
            stock = 10,
            unit = "Unit",
            thumbnail_url = ""
        ),
        Product(
            id = "2",
            user_id = "user1",
            category_id = 2,
            name = "Kaos Polos",
            price = 50000.0,
            stock = 100,
            unit = "Pcs",
            thumbnail_url = ""
        ),
        Product(
            id = "3",
            user_id = "user1",
            category_id = 3,
            name = "Nasi Goreng",
            price = 15000.0,
            stock = 0,
            unit = "Porsi",
            thumbnail_url = ""
        )
    )

    TesTheme {
        ProductScreenContent(
            products = sampleProducts,
            categories = sampleCategories,
            isDarkTheme = false,
            onThemeToggle = {},
            onAddProductClick = {},
            onProductClick = {},
            bottomBar = {
                // Mock bottom bar
                NavigationBar {
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = { Icon(Icons.Default.Person, null) },
                        label = { Text("Profile") }
                    )
                }
            }
        )
    }
}
