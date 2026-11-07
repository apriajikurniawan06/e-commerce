package com.barcode.tes.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.barcode.tes.presentation.viewmodel.TransactionViewModel
import com.barcode.tes.ui.navigation.Screen
import androidx.compose.ui.tooling.preview.Preview
import com.barcode.tes.ui.theme.TesTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    amount: Double, 
    capital: Double, 
    description: String, 
    navController: NavController, 
    viewModel: TransactionViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    PaymentScreenContent(
        initialAmount = amount,
        capital = capital,
        description = description,
        onBackClick = { navController.popBackStack() },
        onPayClick = { finalAmount, finalDesc ->
            scope.launch {
                isProcessing = true
                val transactionId = viewModel.processTransaction(finalAmount, capital, finalDesc)
                if (transactionId != null) {
                    navController.navigate(Screen.Receipt.createRoute(transactionId)) {
                        popUpTo(Screen.Dashboard.route) { inclusive = false }
                    }
                } else {
                    android.widget.Toast.makeText(context, "Gagal memproses transaksi. Periksa database atau koneksi.", android.widget.Toast.LENGTH_LONG).show()
                }
                isProcessing = false
            }
        },
        isProcessing = isProcessing
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreenContent(
    initialAmount: Double,
    capital: Double,
    description: String,
    onBackClick: () -> Unit,
    onPayClick: (Double, String) -> Unit,
    isProcessing: Boolean = false
) {
    var selectedMethod by remember { mutableStateOf("VA") }
    var promoCode by remember { mutableStateOf("") }
    var discountAmount by remember { mutableStateOf(0.0) }
    var promoMessage by remember { mutableStateOf<String?>(null) }
    var appliedPromoName by remember { mutableStateOf<String?>(null) }

    val finalAmount = maxOf(0.0, initialAmount - discountAmount)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pembayaran & Promo") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Bill Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Tagihan", fontSize = 14.sp)
                    if (discountAmount > 0.0) {
                        Text(
                            "Rp ${java.text.DecimalFormat("#,###").format(initialAmount)}",
                            fontSize = 16.sp,
                            style = LocalTextStyle.current.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough),
                            color = Color.Gray
                        )
                    }
                    Text(
                        "Rp ${java.text.DecimalFormat("#,###").format(finalAmount)}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val displayDesc = remember(description) {
                        try { java.net.URLDecoder.decode(description, "UTF-8") } catch (e: Exception) { description }
                    }
                    Text(displayDesc, fontSize = 12.sp, color = Color.DarkGray)
                }
            }

            // Promo Code Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Punya Kode Promo / Diskon?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = promoCode,
                            onValueChange = { promoCode = it.uppercase() },
                            placeholder = { Text("Cth: HEMAT10, DISKON5K") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.LocalOffer, null) }
                        )
                        Button(
                            onClick = {
                                when (promoCode.trim()) {
                                    "HEMAT10" -> {
                                        discountAmount = initialAmount * 0.10
                                        appliedPromoName = "HEMAT10 (10%)"
                                        promoMessage = "Diskon 10% berhasil diterapkan!"
                                    }
                                    "DISKON5K" -> {
                                        discountAmount = 5000.0
                                        appliedPromoName = "DISKON5K (Rp 5rb)"
                                        promoMessage = "Potongan Rp 5.000 berhasil diterapkan!"
                                    }
                                    "PROMOJOSS" -> {
                                        discountAmount = 15000.0
                                        appliedPromoName = "PROMOJOSS (Rp 15rb)"
                                        promoMessage = "Potongan Rp 15.000 berhasil diterapkan!"
                                    }
                                    else -> {
                                        discountAmount = 0.0
                                        appliedPromoName = null
                                        promoMessage = "Kode promo tidak valid atau kadaluarsa."
                                    }
                                }
                            }
                        ) {
                            Text("Terapkan")
                        }
                    }
                    promoMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = msg,
                            fontSize = 12.sp,
                            color = if (discountAmount > 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Text("Pilih Metode Pembayaran", fontWeight = FontWeight.Bold)

            PaymentMethodItem(
                title = "Virtual Account (BCA/BNI/BRI/Mandiri)",
                icon = Icons.Default.CreditCard,
                isSelected = selectedMethod == "VA",
                onClick = { selectedMethod = "VA" }
            )

            PaymentMethodItem(
                title = "DANA / OVO / GoPay / ShopeePay",
                icon = Icons.Default.Wallet,
                isSelected = selectedMethod == "EWALLET",
                onClick = { selectedMethod = "EWALLET" }
            )

            Spacer(modifier = Modifier.weight(1f))

            val fullDescription = if (appliedPromoName != null) {
                "$description [Promo: $appliedPromoName]"
            } else {
                description
            }

            Button(
                onClick = { onPayClick(finalAmount, fullDescription) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isProcessing,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFFF))
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("BAYAR SEKARANG - Rp ${java.text.DecimalFormat("#,###").format(finalAmount)}", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PaymentMethodItem(title: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        onClick = onClick,
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            RadioButton(selected = isSelected, onClick = onClick)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentScreenPreview() {
    TesTheme {
        PaymentScreenContent(
            initialAmount = 100000.0,
            capital = 90000.0,
            description = "Tiket Pesawat Jakarta - Bali",
            onBackClick = {},
            onPayClick = { _, _ -> }
        )
    }
}
