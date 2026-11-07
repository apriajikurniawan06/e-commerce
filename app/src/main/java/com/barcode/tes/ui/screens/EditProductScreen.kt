package com.barcode.tes.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.barcode.tes.data.remote.SupabaseClient
import com.barcode.tes.domain.model.Product
import com.barcode.tes.domain.model.ProductCategory
import com.barcode.tes.presentation.viewmodel.AuthViewModel
import com.barcode.tes.presentation.viewmodel.ProductViewModel
import com.barcode.tes.ui.theme.TesTheme
import kotlinx.coroutines.launch

@Composable
fun EditProductScreen(
    productId: String, 
    navController: NavController, 
    viewModel: ProductViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val products by viewModel.products.collectAsState()
    val product = remember(products) { products.find { it.id == productId } }
    val categories by viewModel.categories.collectAsState()
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isUploading by remember { mutableStateOf(false) }

    EditProductContent(
        product = product,
        categories = categories,
        isUploading = isUploading,
        onBack = { navController.popBackStack() },
        onSave = { name, description, price, stock, location, selectedCategory, imageUri, currentImageUrl ->
            scope.launch {
                isUploading = true
                var finalImageUrl = currentImageUrl
                
                imageUri?.let { uri ->
                    try {
                        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        if (bytes != null) {
                            val userId = authViewModel.currentUser?.id ?: "unknown"
                            val fileName = "$userId/prod_${System.currentTimeMillis()}.jpg"
                            finalImageUrl = viewModel.uploadImage(fileName, bytes)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                val updatedProduct = product?.copy(
                    name = name,
                    description = description,
                    price = price.toDoubleOrNull() ?: 0.0,
                    stock = stock.toIntOrNull() ?: 0,
                    location = location,
                    category_id = selectedCategory?.id,
                    thumbnail_url = finalImageUrl
                ) ?: return@launch
                
                if (viewModel.updateProduct(updatedProduct)) {
                    navController.popBackStack()
                }
                isUploading = false
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductContent(
    product: Product?,
    categories: List<ProductCategory>,
    onBack: () -> Unit,
    onSave: (name: String, description: String, price: String, stock: String, location: String, selectedCategory: ProductCategory?, imageUri: Uri?, currentImageUrl: String?) -> Unit,
    isUploading: Boolean
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("kg") }
    var weight by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }
    
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var currentImageUrl by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    LaunchedEffect(product) {
        product?.let {
            name = it.name
            description = it.description ?: ""
            price = it.price.toString()
            stock = it.stock.toString()
            unit = it.unit
            weight = it.weight?.toString() ?: ""
            location = it.location ?: ""
            selectedCategory = categories.find { cat -> cat.id == it.category_id }
            currentImageUrl = it.thumbnail_url
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Produk") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        if (product == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Upload Gambar Box
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clickable { launcher.launch("image/*") },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.Gray)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        if (imageUri != null) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else if (currentImageUrl != null) {
                            AsyncImage(
                                model = SupabaseClient.productImageUrl(currentImageUrl),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(48.dp))
                                Text("Klik untuk ganti gambar")
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Produk") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Selector
                Box {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "Pilih Kategori",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori") },
                        modifier = Modifier.fillMaxWidth().clickable { categoryExpanded = true },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { categoryExpanded = true })
                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategory = category
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Harga") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Stok") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lokasi") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onSave(name, description, price, stock, location, selectedCategory, imageUri, currentImageUrl)
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFFF)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isUploading
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("SIMPAN PERUBAHAN", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProductScreenPreview() {
    TesTheme {
        EditProductContent(
            product = Product(
                id = "1",
                user_id = "user123",
                name = "Kopi Robusta",
                description = "Kopi robusta asli Lampung",
                price = 50000.0,
                stock = 100,
                unit = "kg",
                category_id = 1,
                thumbnail_url = "https://via.placeholder.com/150"
            ),
            categories = listOf(
                ProductCategory(id = 1, name = "Minuman"),
                ProductCategory(id = 2, name = "Makanan")
            ),
            onBack = {},
            onSave = { _, _, _, _, _, _, _, _ -> },
            isUploading = false
        )
    }
}
