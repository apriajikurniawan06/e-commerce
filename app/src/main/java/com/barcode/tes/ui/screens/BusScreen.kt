package com.barcode.tes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBus
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
fun BusScreen(navController: NavController) {
    var origin by remember { mutableStateOf("Terminal Pulo Gebang (Jakarta)") }
    var destination by remember { mutableStateOf("Terminal Bungurasih (Surabaya)") }
    var selectedBus by remember { mutableStateOf("Sinar Jaya (Executive Suite)") }
    var selectedClass by remember { mutableStateOf("Executive") }
    var selectedSchedule by remember { mutableStateOf("17:00 WIB (Malam)") }
    var passengerName by remember { mutableStateOf("") }

    val buses = listOf("Sinar Jaya (Executive Suite)" to 280000.0, "Rosalia Indah (First Class)" to 380000.0, "Harapan Jaya (VIP)" to 320000.0)
    val schedules = listOf("09:00 WIB (Pagi)", "17:00 WIB (Malam)", "20:00 WIB (Malam)")
    val classes = listOf("VIP", "Executive", "First Class")

    val basePrice = buses.find { it.first == selectedBus }?.second ?: 280000.0
    val classMultiplier = when(selectedClass) { "First Class" -> 1.3; "Executive" -> 1.1; else -> 1.0 }
    val finalPrice = basePrice * classMultiplier
    val capitalPrice = finalPrice * 0.8

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pesan Tiket Bus AKAP") },
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DirectionsBus, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Bus Antar Kota Antar Provinsi (AKAP)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Perjalanan darat fleksibel & ekonomis.", fontSize = 12.sp)
                    }
                }
            }

            OutlinedTextField(
                value = origin,
                onValueChange = { origin = it },
                label = { Text("Terminal / Kota Asal") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Terminal / Kota Tujuan") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = passengerName,
                onValueChange = { passengerName = it },
                label = { Text("Nama Penumpang (Sesuai KTP)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Pilih PO Bus", fontWeight = FontWeight.Bold)
            buses.forEach { (bus, price) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedBus == bus,
                        onClick = { selectedBus = bus }
                    )
                    Text("$bus (Rp ${java.text.DecimalFormat("#,###").format(price)})")
                }
            }

            Text("Kelas Bus", fontWeight = FontWeight.Bold)
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
                    val desc = "Tiket Bus $selectedBus ($origin -> $destination) $selectedClass Jdw: $selectedSchedule [Penumpang: ${passengerName.ifBlank { "Tamu" }}]"
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
