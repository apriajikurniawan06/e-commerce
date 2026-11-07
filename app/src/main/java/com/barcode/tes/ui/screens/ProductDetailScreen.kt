package com.barcode.tes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import com.barcode.tes.domain.model.Address
import com.barcode.tes.domain.model.Product
import com.barcode.tes.domain.model.Profile
import com.barcode.tes.presentation.viewmodel.AddressViewModel
import com.barcode.tes.presentation.viewmodel.AuthViewModel
import com.barcode.tes.presentation.viewmodel.ProductViewModel
import com.barcode.tes.data.remote.SupabaseClient
import com.barcode.tes.ui.theme.TesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String, 
    navController: NavController, 
    viewModel: ProductViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    addressViewModel: AddressViewModel = viewModel()
) {
    val products by viewModel.products.collectAsState()
    val product = products.find { it.id == productId }

    var sellerProfile by remember { mutableStateOf<Profile?>(null) }
    var sellerAddress by remember { mutableStateOf<Address?>(null) }

    LaunchedEffect(product) {
        product?.user_id?.let { userId ->
            authViewModel.getProfileById(userId) { sellerProfile = it }
            addressViewModel.fetchDefaultAddress(userId) { sellerAddress = it }
        }
    }

    ProductDetailContent(
        product = product,
        sellerProfile = sellerProfile,
        sellerAddress = sellerAddress,
        onBackClick = { navController.popBackStack() },
        onBuyClick = { /* Handle buy action */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailContent(
    product: Product?,
    sellerProfile: Profile?,
    sellerAddress: Address?,
    onBackClick: () -> Unit,
    onBuyClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        product?.let { 
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(250.dp)
                ) {
                    AsyncImage(
                        model = SupabaseClient.productImageUrl(it.thumbnail_url),
                        contentDescription = it.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = android.R.drawable.ic_menu_gallery),
                        placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val formattedPrice = remember(it.price) {
                    java.text.DecimalFormat("#,###.##").format(it.price)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(it.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Rp. $formattedPrice / ${it.unit}", fontSize = 18.sp, color = Color.Red)
                }

                Text("Qty: ${it.stock} ${it.unit}", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                Text("Harga: Rp. $formattedPrice", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                Text("Ongkir: Rp. 400.000", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                
                val totalAmount = it.price + 400000
                val formattedTotal = remember(totalAmount) {
                    java.text.DecimalFormat("#,###.##").format(totalAmount)
                }
                Text("Total: Rp. $formattedTotal", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.height(16.dp))
                
                Text("${sellerProfile?.shop_name ?: "Toko"}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("ALAMAT: ${sellerAddress?.city ?: sellerAddress?.address ?: "Tidak ada alamat"}", color = MaterialTheme.colorScheme.primary)
                Text(it.description ?: "Tidak ada deskripsi", color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Rating ")
                    repeat(5) { i ->
                        Icon(
                            Icons.Default.Star,
                            null,
                            tint = if (i < it.rating) Color.Yellow else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onBuyClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFFF)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("BELI", fontWeight = FontWeight.Bold)
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Produk tidak ditemukan")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailScreenPreview() {
    val sampleProduct = Product(
        id = "1",
        user_id = "user1",
        name = "Produk Unggulan",
        description = "Ini adalah deskripsi produk yang sangat menarik untuk dibaca oleh calon pembeli.",
        price = 150000.0,
        stock = 25,
        unit = "kg",
        rating = 4.5,
        thumbnail_url = ""
    )
    val sampleProfile = Profile(
        id = "user1",
        shop_name = "Toko Berkah"
    )
    val sampleAddress = Address(
        user_id = "user1",
        city = "Jakarta Selatan"
    )
    
    TesTheme {
        ProductDetailContent(
            product = sampleProduct,
            sellerProfile = sampleProfile,
            sellerAddress = sampleAddress,
            onBackClick = {},
            onBuyClick = {}
        )
    }
}
