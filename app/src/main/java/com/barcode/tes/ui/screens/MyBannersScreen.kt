package com.barcode.tes.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.barcode.tes.data.remote.SupabaseClient
import com.barcode.tes.domain.model.Banner
import com.barcode.tes.presentation.viewmodel.AuthViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.barcode.tes.ui.theme.TesTheme
import com.barcode.tes.presentation.viewmodel.BannerViewModel
import com.barcode.tes.presentation.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBannersScreen(
    navController: NavController, 
    viewModel: BannerViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    productViewModel: ProductViewModel = viewModel()
) {
    val banners by viewModel.banners.collectAsState()
    val currentUser = authViewModel.currentUser
    val myBanners = remember(banners, currentUser) {
        banners.filter { it.user_id == currentUser?.id }
    }

    var showUploadSection by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iklan Saya", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { showUploadSection = !showUploadSection }) {
                        Icon(if (showUploadSection) Icons.Default.Close else Icons.Default.Add, contentDescription = "Tambah Iklan")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (showUploadSection) {
                Card(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Pasang Iklan Baru", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        
                        Card(
                            modifier = Modifier.fillMaxWidth().height(150.dp).clickable { launcher.launch("image/*") },
                            border = BorderStroke(1.dp, Color.Gray),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                if (imageUri != null) {
                                    AsyncImage(
                                        model = imageUri,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(40.dp))
                                        Text("Pilih Gambar Banner", fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Nama Toko / Judul Iklan") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                scope.launch {
                                    isUploading = true
                                    val user = authViewModel.currentUser
                                    if (user != null && imageUri != null) {
                                        val bytes = context.contentResolver.openInputStream(imageUri!!)?.use { it.readBytes() }
                                        if (bytes != null) {
                                            val fileName = "${user.id}/banner_${System.currentTimeMillis()}.jpg"
                                            val imageUrl = productViewModel.uploadImage(fileName, bytes)
                                            if (imageUrl != null) {
                                                val banner = Banner(
                                                    user_id = user.id,
                                                    title = title,
                                                    image_url = imageUrl,
                                                    is_active = true
                                                )
                                                if (viewModel.addBanner(banner)) {
                                                    showUploadSection = false
                                                    title = ""
                                                    imageUri = null
                                                }
                                            }
                                        }
                                    }
                                    isUploading = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = title.isNotBlank() && imageUri != null && !isUploading,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFFF))
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            } else {
                                Text("UNGGAH IKLAN", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            if (myBanners.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada iklan.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(myBanners) { banner ->
                        BannerItem(banner)
                    }
                }
            }
        }
    }
}

@Composable
fun BannerItem(banner: Banner) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            AsyncImage(
                model = SupabaseClient.bannerImageUrl(banner.image_url),
                contentDescription = banner.title,
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentScale = ContentScale.Crop
            )
            PaddingValues(12.dp).let {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(banner.title ?: "Tanpa Nama", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(if (banner.is_active) "Status: Aktif" else "Status: Nonaktif", fontSize = 12.sp, color = if (banner.is_active) Color(0xFF4CAF50) else Color.Red)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BannerItemPreview() {
    TesTheme {
        BannerItem(
            banner = Banner(
                id = "1",
                user_id = "user123",
                title = "Contoh Banner Iklan",
                image_url = "https://example.com/image.jpg",
                is_active = true
            )
        )
    }
}
