package com.barcode.tes.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.barcode.tes.presentation.viewmodel.PpobViewModel
import com.barcode.tes.ui.navigation.Screen
import androidx.compose.ui.tooling.preview.Preview
import com.barcode.tes.domain.model.PpobProduct
import com.barcode.tes.ui.theme.TesTheme
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PulsaScreen(
    navController: NavController,
    viewModel: PpobViewModel = viewModel()
) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    PulsaScreenContent(
        products = products,
        isLoading = isLoading,
        onBackClick = { navController.popBackStack() },
        onProductClick = { product, phoneNumber, sellingPrice ->
            val desc = "${product.name} ($phoneNumber)"
            val encodedDesc = java.net.URLEncoder.encode(desc, "UTF-8")
            navController.navigate(
                Screen.Payment.createRoute(
                    sellingPrice,
                    product.price, // Harga modal
                    encodedDesc
                )
            )
        },
        fetchProducts = { viewModel.fetchProducts(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PulsaScreenContent(
    products: List<PpobProduct>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onProductClick: (PpobProduct, String, Double) -> Unit,
    fetchProducts: (String) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    val adminFee = 2500.0 // Keuntungan yang Anda ambil per transaksi

    // Deteksi Provider secara sederhana
    val provider = remember(phoneNumber) {
        when {
            phoneNumber.startsWith("0812") || phoneNumber.startsWith("0813") || phoneNumber.startsWith("0821") -> "Telkomsel"
            phoneNumber.startsWith("0857") || phoneNumber.startsWith("0858") -> "Indosat"
            phoneNumber.startsWith("0817") || phoneNumber.startsWith("0818") || phoneNumber.startsWith("0819") -> "XL"
            phoneNumber.startsWith("0895") || phoneNumber.startsWith("0896") || phoneNumber.startsWith("0897") -> "Tri"
            phoneNumber.startsWith("0881") || phoneNumber.startsWith("0882") -> "Smartfren"
            else -> ""
        }
    }

    // Ambil data produk berdasarkan tab & provider
    LaunchedEffect(selectedTab, provider) {
        val category = if (selectedTab == 0) "pulsa" else "data"
        // Anda bisa memfilter produk berdasarkan provider di sini atau di ViewModel
        fetchProducts(category)
    }

    // Urutkan produk termurah & filter (Simulasi filter provider)
    val displayedProducts = remember(products, provider) {
        products
            .filter { if (provider.isNotEmpty()) it.name.contains(provider, ignoreCase = true) else true }
            .sortedBy { it.price }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Top Up Pulsa & Data", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Input Section
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Nomor Handphone", fontSize = 12.sp, color = Color.Gray)
                        if (provider.isNotEmpty()) {
                            Text(
                                provider,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { if (it.length <= 15) phoneNumber = it },
                        placeholder = { Text("Contoh: 08123456xxx") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        trailingIcon = {
                            Icon(Icons.Default.ContactPhone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = if (MaterialTheme.colorScheme.background == Color.Black) Color(0xFF00BFFF) else Color.Black,
                            unfocusedTextColor = if (MaterialTheme.colorScheme.background == Color.Black) Color(0xFF00BFFF) else Color.Black,
                            focusedBorderColor = Color(0xFF00BFFF),
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color(0xFF00BFFF)
                        )
                    )
                }
            }

            // Tab Selection
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Isi Pulsa", fontWeight = if(selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Paket Data", fontWeight = if(selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (displayedProducts.isEmpty() && phoneNumber.length >= 4) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Produk untuk $provider belum tersedia", color = Color.Gray)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(displayedProducts) { product ->
                            val sellingPrice = product.price + adminFee
                            val isCheapest = product == displayedProducts.firstOrNull()

                            ProductItem(
                                name = product.name,
                                price = sellingPrice,
                                isCheapest = isCheapest,
                                onClick = {
                                    if (phoneNumber.length >= 10) {
                                        onProductClick(product, phoneNumber, sellingPrice)
                                    } else {
                                        println("Cek kembali Nomor Anda!")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItem(name: String, price: Double, isCheapest: Boolean = false, onClick: () -> Unit) {
    val formatter = DecimalFormat("#,###")
    val isDark = MaterialTheme.colorScheme.background == Color.Black
    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                1.dp,
                if (isCheapest) Color(0xFF00BFFF) else if (isDark) Color(0xFF00BFFF).copy(alpha = 0.5f) else Color.LightGray.copy(
                    alpha = 0.5f
                )
            ),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1A1A1A) else Color.White),
            elevation = CardDefaults.cardElevation(if (isDark) 0.dp else 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color.White else Color.Black,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rp ${formatter.format(price)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF00BFFF)
                )
            }
        }

        if (isCheapest) {
            Surface(
                color = Color(0xFF00BFFF),
                shape = RoundedCornerShape(bottomStart = 12.dp, topEnd = 16.dp),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(
                    "TERMURAH",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PulsaScreenPreview() {
    val sampleProducts = listOf(
        PpobProduct(
            code = "TSEL5",
            name = "Telkomsel 5.000",
            price = 5100.0,
            capital_price = 5000.0,
            category = "pulsa",
            provider = "Telkomsel"
        ),
        PpobProduct(
            code = "TSEL10",
            name = "Telkomsel 10.000",
            price = 10100.0,
            capital_price = 10000.0,
            category = "pulsa",
            provider = "Telkomsel"
        )
    )
    TesTheme {
        PulsaScreenContent(
            products = sampleProducts,
            isLoading = false,
            onBackClick = {},
            onProductClick = { _, _, _ -> },
            fetchProducts = {}
        )
    }
}
