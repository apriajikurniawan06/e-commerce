package com.barcode.tes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.barcode.tes.domain.model.Product
import com.barcode.tes.presentation.viewmodel.AuthViewModel
import com.barcode.tes.presentation.viewmodel.ProductViewModel
import com.barcode.tes.ui.navigation.Screen
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview
import com.barcode.tes.ui.theme.TesTheme
import com.barcode.tes.data.remote.SupabaseClient

@Composable
fun MyProductsScreen(
    navController: NavController,
    viewModel: ProductViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val allProducts by viewModel.products.collectAsState()
    val currentUser = authViewModel.currentUser
    val scope = rememberCoroutineScope()
    
    val myProducts = remember(allProducts, currentUser) {
        allProducts.filter { it.user_id == currentUser?.id }
    }

    MyProductsContent(
        myProducts = myProducts,
        onBack = { navController.popBackStack() },
        onAddProduct = { navController.navigate(Screen.AddProduct.route) },
        onEditProduct = { product ->
            navController.navigate(Screen.EditProduct.createRoute(product.id ?: ""))
        },
        onDeleteProduct = { product ->
            scope.launch {
                viewModel.deleteProduct(product.id ?: "")
            }
        },
        onProductClick = { product ->
            navController.navigate(Screen.ProductDetail.createRoute(product.id ?: ""))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProductsContent(
    myProducts: List<Product>,
    onBack: () -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (Product) -> Unit,
    onDeleteProduct: (Product) -> Unit,
    onProductClick: (Product) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Produk Saya", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = onAddProduct) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah Produk")
                    }
                }
            )
        }
    ) { padding ->
        if (myProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Inventory,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Anda belum memiliki produk", color = Color.Gray)
                    Button(
                        onClick = onAddProduct,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Mulai Berjualan")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(padding)
                    .padding(8.dp),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(myProducts) { product ->
                    MyProductCard(
                        product = product,
                        onEdit = { onEditProduct(product) },
                        onDelete = { onDeleteProduct(product) },
                        onClick = { onProductClick(product) }
                    )
                }
            }
        }
    }
}

@Composable
fun MyProductCard(
    product: Product, 
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Produk") },
            text = { Text("Apakah Anda yakin ingin menghapus '${product.name}'?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)) {
                AsyncImage(
                    model = SupabaseClient.productImageUrl(product.thumbnail_url),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = android.R.drawable.ic_menu_gallery),
                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
                )
                
                // Stock badge
                Surface(
                    color = if (product.stock > 0) Color(0xFF4CAF50) else Color.Red,
                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "STOK: ${product.stock}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    product.name,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                val formattedPrice = remember(product.price) {
                    java.text.DecimalFormat("#,###.##").format(product.price)
                }
                
                Text("Rp. $formattedPrice / ${product.unit}", color = Color.Gray, fontSize = 12.sp)
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(30.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyProductsScreenPreview() {
    val sampleProducts = listOf(
        Product(
            id = "1",
            user_id = "user1",
            name = "Produk Sampel 1",
            price = 50000.0,
            stock = 10,
            unit = "pcs",
            thumbnail_url = ""
        ),
        Product(
            id = "2",
            user_id = "user1",
            name = "Produk Sampel 2",
            price = 75000.0,
            stock = 0,
            unit = "kg",
            thumbnail_url = ""
        ),
        Product(
            id = "3",
            user_id = "user1",
            name = "Produk Sampel 3 yang Namanya Sangat Panjang Sekali",
            price = 125000.0,
            stock = 5,
            unit = "liter",
            thumbnail_url = ""
        )
    )
    
    TesTheme {
        MyProductsContent(
            myProducts = sampleProducts,
            onBack = {},
            onAddProduct = {},
            onEditProduct = {},
            onDeleteProduct = {},
            onProductClick = {}
        )
    }
}
