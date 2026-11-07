package com.barcode.tes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBoat
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
fun ShipScreen(navController: NavController) {
    var origin by remember { mutableStateOf("Tanjung Priok (Jakarta)") }
    var destination by remember { mutableStateOf("Tanjung Perak (Surabaya)") }
    var selectedShip by remember { mutableStateOf("KM. Dobonsolo (PELNI)") }
    var selectedClass by remember { mutableStateOf("Kelas 1") }
    var selectedSchedule by remember { mutableStateOf("10:00 WIB (2 Hari 1 Malam)") }
    var passengerName by remember { mutableStateOf("") }

    val ships = listOf("KM. Dobonsolo (PELNI)" to 450000.0, "KM. Kelud" to 420000.0, "KM. Gunung Dempo" to 400000.0)
    val schedules = listOf("10:00 WIB (2 Hari 1 Malam)", "14:00 WIB (3 Hari 2 Malam)")
    val classes = listOf("Ekonomi", "Kelas 2", "Kelas 1")

    val basePrice = ships.find { it.first == selectedShip }?.second ?: 450000.0
    val classMultiplier = when(selectedClass) { "Kelas 1" -> 1.5; "Kelas 2" -> 1.25; else -> 1.0 }
    val finalPrice = basePrice * classMultiplier
    val capitalPrice = finalPrice * 0.8

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pesan Tiket Kapal Penumpang") },
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DirectionsBoat, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Pelayaran Nasional (PELNI)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Jelajahi nusantara via lautan luas.", fontSize = 12.sp)
                    }
                }
            }

            OutlinedTextField(
                value = origin,
                onValueChange = { origin = it },
                label = { Text("Pelabuhan Asal") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Pelabuhan Tujuan") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = passengerName,
                onValueChange = { passengerName = it },
                label = { Text("Nama Penumpang (Sesuai KTP)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Pilih Kapal", fontWeight = FontWeight.Bold)
            ships.forEach { (ship, price) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedShip == ship,
                        onClick = { selectedShip = ship }
                    )
                    Text("$ship (Rp ${java.text.DecimalFormat("#,###").format(price)})")
                }
            }

            Text("Kelas Kabin", fontWeight = FontWeight.Bold)
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
                    val desc = "Tiket Kapal $selectedShip ($origin -> $destination) $selectedClass Jdw: $selectedSchedule [Penumpang: ${passengerName.ifBlank { "Tamu" }}]"
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
