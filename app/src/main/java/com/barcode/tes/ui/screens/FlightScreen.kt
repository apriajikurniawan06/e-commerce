package com.barcode.tes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.barcode.tes.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightScreen(navController: NavController) {
    var origin by remember { mutableStateOf("Jakarta (CGK)") }
    var destination by remember { mutableStateOf("Denpasar (DPS)") }
    var selectedAirline by remember { mutableStateOf("Garuda Indonesia") }
    var selectedClass by remember { mutableStateOf("Ekonomi") }
    var selectedSchedule by remember { mutableStateOf("08:00 WIB - 11:00 WIB") }
    var passengerName by remember { mutableStateOf("") }

    val airlines = listOf("Garuda Indonesia" to 1250000.0, "Lion Air" to 650000.0, "Citilink" to 750000.0, "Batik Air" to 900000.0)
    val schedules = listOf("08:00 WIB - 11:00 WIB", "13:30 WIB - 16:30 WIB", "19:00 WIB - 22:00 WIB")
    val classes = listOf("Ekonomi", "Bisnis")

    val basePrice = airlines.find { it.first == selectedAirline }?.second ?: 750000.0
    val classMultiplier = if (selectedClass == "Bisnis") 1.8 else 1.0
    val finalPrice = basePrice * classMultiplier
    val capitalPrice = finalPrice * 0.85

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pesan Tiket Pesawat") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Flight, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Penerbangan Domestik", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Nikmati perjalanan udara terbaik & aman.", fontSize = 12.sp)
                    }
                }
            }

            OutlinedTextField(
                value = origin,
                onValueChange = { origin = it },
                label = { Text("Kota Asal / Bandara") },
                leadingIcon = { Icon(Icons.Default.FlightTakeoff, null) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Kota Tujuan / Bandara") },
                leadingIcon = { Icon(Icons.Default.FlightLand, null) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = passengerName,
                onValueChange = { passengerName = it },
                label = { Text("Nama Penumpang (Sesuai KTP)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Pilih Maskapai", fontWeight = FontWeight.Bold)
            airlines.forEach { (airline, price) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedAirline == airline,
                        onClick = { selectedAirline = airline }
                    )
                    Text("$airline (Rp ${java.text.DecimalFormat("#,###").format(price)})")
                }
            }

            Text("Kelas Penerbangan", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                classes.forEach { cls ->
                    FilterChip(
                        selected = selectedClass == cls,
                        onClick = { selectedClass = cls },
                        label = { Text(cls) }
                    )
                }
            }

            Text("Jadwal Keberangkatan", fontWeight = FontWeight.Bold)
            schedules.forEach { sched ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedSchedule == sched,
                        onClick = { selectedSchedule = sched }
                    )
                    Text(sched)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val desc = "Tiket Pesawat $selectedAirline ($origin -> $destination) Kelas $selectedClass Jdw: $selectedSchedule [Penumpang: ${passengerName.ifBlank { "Tamu" }}]"
                    navController.navigate(Screen.Payment.createRoute(finalPrice, capitalPrice, desc))
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFFF))
            ) {
                Text("LANJUT KE PEMBAYARAN - Rp ${java.text.DecimalFormat("#,###").format(finalPrice)}", fontWeight = FontWeight.Bold)
            }
        }
    }
}
