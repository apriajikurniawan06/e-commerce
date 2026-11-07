package com.barcode.tes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ElectricBolt
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
import androidx.compose.ui.tooling.preview.Preview
import com.barcode.tes.domain.model.PpobProduct
import com.barcode.tes.ui.theme.TesTheme
import com.barcode.tes.presentation.viewmodel.PpobViewModel
import com.barcode.tes.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlnScreen(
    navController: NavController,
    viewModel: PpobViewModel = viewModel()
) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchProducts("pln")
    }

    PlnContent(
        products = products,
        isLoading = isLoading,
        onBackClick = { navController.popBackStack() },
        onProductClick = { product, meterNumber ->
            val desc = "${product.name} ($meterNumber)"
            val encodedDesc = java.net.URLEncoder.encode(desc, "UTF-8")
            navController.navigate(Screen.Payment.createRoute(product.price, product.capital_price, encodedDesc))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlnContent(
    products: List<PpobProduct>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onProductClick: (PpobProduct, String) -> Unit
) {
    var meterNumber by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listrik PLN", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nomor Meter / ID Pelanggan", fontSize = 12.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = meterNumber,
                        onValueChange = { meterNumber = it },
                        placeholder = { Text("Masukkan 11-12 digit") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = { Icon(Icons.Default.ElectricBolt, null, tint = Color.Yellow) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = if (isSystemInDarkTheme()) Color(0xFF00BFFF) else Color.Black,
                            unfocusedTextColor = if (isSystemInDarkTheme()) Color(0xFF00BFFF) else Color.Black,
                            focusedBorderColor = Color(0xFF00BFFF),
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color(0xFF00BFFF)
                        )
                    )
                }
            }

            Text("Pilih Nominal Token", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products) { product ->
                        val isCheapest = product == products.minByOrNull { it.price }
                        ProductItem(
                            name = product.name,
                            price = product.price,
                            isCheapest = isCheapest,
                            onClick = {
                                if (meterNumber.length >= 10) {
                                    onProductClick(product, meterNumber)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlnScreenPreview() {
    val sampleProducts = listOf(
        PpobProduct(code = "PLN20", name = "Token PLN 20.000", price = 22000.0, capital_price = 20000.0, category = "pln"),
        PpobProduct(code = "PLN50", name = "Token PLN 50.000", price = 52000.0, capital_price = 50000.0, category = "pln"),
        PpobProduct(code = "PLN100", name = "Token PLN 100.000", price = 102000.0, capital_price = 100000.0, category = "pln"),
        PpobProduct(code = "PLN200", name = "Token PLN 200.000", price = 202000.0, capital_price = 200000.0, category = "pln")
    )

    TesTheme {
        PlnContent(
            products = sampleProducts,
            isLoading = false,
            onBackClick = {},
            onProductClick = { _, _ -> }
        )
    }
}
