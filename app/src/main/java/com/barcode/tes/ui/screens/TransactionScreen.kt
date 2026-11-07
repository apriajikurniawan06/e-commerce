package com.barcode.tes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.barcode.tes.domain.model.Order
import com.barcode.tes.presentation.viewmodel.TransactionViewModel
import com.barcode.tes.ui.components.BottomNavigationBar
import com.barcode.tes.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    type: String, 
    navController: NavController,
    viewModel: TransactionViewModel = viewModel()
) {
    val isHistory = type.equals("History", ignoreCase = true)
    val orders by viewModel.orders.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isHistory) "Riwayat Transaksi" else type) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController, currentRoute = if (isHistory) "riwayat" else null)
        }
    ) { padding ->
        if (isHistory) {
            if (orders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Belum ada riwayat transaksi")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders) { order ->
                        HistoryCard(order) {
                            navController.navigate(Screen.Receipt.createRoute(order.id ?: ""))
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Halaman Transaksi: $type", fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

@Composable
fun HistoryCard(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon dinamis berdasarkan jenis transaksi
            val icon = when {
                order.description?.contains("Pulsa", true) == true -> Icons.Default.PhoneAndroid
                order.description?.contains("PDAM", true) == true -> Icons.Default.WaterDrop
                else -> Icons.AutoMirrored.Filled.ReceiptLong
            }
            
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                // Penentuan Nama Jenis Transaksi (Pulsa/PDAM/dll)
                val typeLabel = when {
                    order.description?.contains("Pulsa", true) == true -> "Transaksi Pulsa"
                    order.description?.contains("PDAM", true) == true -> "Transaksi PDAM"
                    order.description?.contains("Internet", true) == true -> "Transaksi Paket Data"
                    else -> "Transaksi Aplikasi"
                }
                
                Text(
                    text = typeLabel, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 16.sp, 
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Tampilkan rincian (misal: Telkomsel - 0812...)
                if (!order.description.isNullOrBlank()) {
                    Text(
                        text = order.description!!,
                        fontSize = 12.sp, 
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }

                Text(
                    text = "ID: #${order.id?.take(8)}...", 
                    fontSize = 11.sp, 
                    color = Color.Gray
                )
                Text(text = order.created_at?.take(10) ?: "", fontSize = 11.sp, color = Color.Gray)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Rp ${java.text.DecimalFormat("#,###").format(order.grand_total ?: 0.0)}", 
                    fontWeight = FontWeight.ExtraBold, 
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp
                )
                Surface(
                    color = if (order.payment_status == "paid") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = order.payment_status.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        color = if (order.payment_status == "paid") Color(0xFF2E7D32) else Color(0xFFEF6C00)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionScreenPreview() {
    // Fixed StackOverflowError by avoiding recursive call.
    // In a real scenario, we would use a stateless content version or mock the NavController and ViewModels.
    Text("Transaction Screen Preview (Stateless extraction recommended)")
}
