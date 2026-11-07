package com.barcode.tes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.barcode.tes.presentation.viewmodel.PpobViewModel
import com.barcode.tes.ui.navigation.Screen
import com.barcode.tes.ui.theme.TesTheme
import java.text.DecimalFormat

@Composable
fun PdamScreen(
    navController: NavController,
    viewModel: PpobViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()

    PdamContent(
        isLoading = isLoading,
        onBack = { navController.popBackStack() },
        onCheckBill = { customerId, selectedArea ->
            val amount = 75000.0 // Contoh tagihan dari API
            val desc = "PDAM - $selectedArea ($customerId)"
            val encodedDesc = java.net.URLEncoder.encode(desc, "UTF-8")
            navController.navigate(Screen.Payment.createRoute(amount, amount * 0.95, encodedDesc))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdamContent(
    isLoading: Boolean,
    onBack: () -> Unit,
    onCheckBill: (customerId: String, selectedArea: String) -> Unit
) {
    var customerId by remember { mutableStateOf("") }
    var selectedArea by remember { mutableStateOf("Pilih Wilayah PDAM") }
    var showAreaDialog by remember { mutableStateOf(false) }
    
    val areas = listOf(
        "PDAM Kota Malang", "PDAM Kab. Malang", "PDAM Surabaya", "PDAM Jakarta (AETRA)", 
        "PDAM Jakarta (PALYJA)", "PDAM Bandung", "PDAM Medan", "PDAM Makassar", 
        "PDAM Semarang", "PDAM Yogyakarta", "PDAM Palembang", "PDAM Denpasar",
        "PDAM Palu", "PDAM Manado", "PDAM Balikpapan", "PDAM Samarinda",
        "PDAM Banjarmasin", "PDAM Pontianak", "PDAM Ambon", "PDAM Jayapura",
        "PDAM Kupang", "PDAM Mataram", "PDAM Batam", "PDAM Pekanbaru"
    ).sorted()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tagihan PDAM", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Area Selection
            OutlinedCard(
                onClick = { showAreaDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF00BFFF))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(selectedArea, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }

            // Customer ID Input
            OutlinedTextField(
                value = customerId,
                onValueChange = { customerId = it },
                label = { Text("Nomor Pelanggan") },
                placeholder = { Text("Masukkan ID Pelanggan") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (MaterialTheme.colorScheme.background == Color.Black) Color(0xFF00BFFF) else Color.Black,
                    unfocusedTextColor = if (MaterialTheme.colorScheme.background == Color.Black) Color(0xFF00BFFF) else Color.Black,
                    focusedBorderColor = Color(0xFF00BFFF),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Professional "Check Bill" Button
            Button(
                onClick = { onCheckBill(customerId, selectedArea) },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFFF)),
                enabled = customerId.isNotEmpty() && selectedArea != "Pilih Wilayah PDAM" && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("CEK TAGIHAN SEKARANG", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }

    // Dialog Pemilihan Wilayah
    if (showAreaDialog) {
        AlertDialog(
            onDismissRequest = { showAreaDialog = false },
            title = { Text("Pilih Wilayah") },
            text = {
                Column {
                    areas.forEach { area ->
                        TextButton(
                            onClick = {
                                selectedArea = area
                                showAreaDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(area, color = Color.Black)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun PdamScreenPreview() {
    TesTheme {
        PdamContent(
            isLoading = false,
            onBack = {},
            onCheckBill = { _, _ -> }
        )
    }
}
