package com.barcode.tes.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.barcode.tes.presentation.viewmodel.AuthViewModel
import com.barcode.tes.presentation.viewmodel.TransactionViewModel
import com.barcode.tes.ui.navigation.Screen
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesHistoryScreen(
    navController: NavController, 
    viewModel: TransactionViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val currentUser = authViewModel.currentUser
    
    val mySales = remember(orders, currentUser) {
        orders.filter { it.seller_id == currentUser?.id && it.payment_status == "paid" }
    }
    
    val netProfit = mySales.sumOf { (it.grand_total ?: 0.0) - (it.capital_price ?: 0.0) }
    val formattedProfit = remember(netProfit) { DecimalFormat("#,###").format(netProfit) }

    // Refactored to use a stateless Content composable for better preview support
    SalesHistoryContent(
        mySales = mySales,
        formattedProfit = formattedProfit,
        onBackClick = { navController.popBackStack() }
    )
}

/**
 * Stateless version of SalesHistoryScreen for easier testing and previewing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesHistoryContent(
    mySales: List<Order>,
    formattedProfit: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Penjualan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    // Total Laba di Kanan Atas (Fixed in TopBar)
                    Surface(
                        color = Color(0xFFF08080),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), horizontalAlignment = Alignment.End) {
                            Text("Total Laba", fontSize = 10.sp, color = Color.Black)
                            Text("Rp $formattedProfit", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (mySales.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.History, null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                    Text("Belum ada riwayat penjualan.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mySales) { sale ->
                    SalesItemCard(sale)
                }
            }
        }
    }
}

@Composable
fun SalesItemCard(order: Order) {
    val isDark = MaterialTheme.colorScheme.background == Color.Black
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if(isDark) Color(0xFF1A1A1A) else Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        border = if(isDark) BorderStroke(0.5.dp, Color.Gray.copy(alpha = 0.3f)) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF90EE90).copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.TrendingUp, null, tint = Color(0xFF4CAF50))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                // Tampilkan Jenis Transaksi secara otomatis
                val type = when {
                    order.description?.contains("Pulsa", ignoreCase = true) == true -> "Transaksi Pulsa"
                    order.description?.contains("Internet", ignoreCase = true) == true || order.description?.contains("Data", ignoreCase = true) == true -> "Transaksi Data"
                    order.description?.contains("PLN", ignoreCase = true) == true -> "Transaksi PLN"
                    order.description?.contains("PDAM", ignoreCase = true) == true -> "Transaksi PDAM"
                    else -> "Transaksi Aplikasi"
                }
                Text(type, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color(0xFF00BFFF))
                
                // Decode deskripsi untuk menghilangkan tanda '+' dari URL encoding
                val displayDesc = order.description?.replace("+", " ") ?: "Penjualan Berhasil"
                Text(displayDesc, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
                
                Text("ID: ${order.id?.take(8)}...", fontSize = 11.sp, color = Color.Gray)
                Text(order.created_at?.split("T")?.get(0) ?: "-", fontSize = 11.sp, color = Color.Gray)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                val profit = (order.grand_total ?: 0.0) - (order.capital_price ?: 0.0)
                Text("Rp ${DecimalFormat("#,###").format(order.grand_total ?: 0.0)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("+Rp ${DecimalFormat("#,###").format(profit)}", fontSize = 12.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SalesHistoryScreenPreview() {
    // Fixed StackOverflowError by calling the stateless content instead of the preview function itself.
    // Also provided mock data so the preview renders correctly without ViewModels.
    val mockSales = listOf(
        Order(id = "1", buyer_id = "b1", seller_id = "s1", description = "Transaksi Pulsa", grand_total = 50000.0, capital_price = 48000.0, payment_status = "paid", created_at = "2023-10-27T10:00:00"),
        Order(id = "2", buyer_id = "b1", seller_id = "s1", description = "Transaksi PLN", grand_total = 100000.0, capital_price = 95000.0, payment_status = "paid", created_at = "2023-10-27T11:00:00")
    )
    
    SalesHistoryContent(
        mySales = mockSales,
        formattedProfit = "7,000",
        onBackClick = {}
    )
}