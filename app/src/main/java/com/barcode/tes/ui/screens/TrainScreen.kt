package com.barcode.tes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Train
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
fun TrainScreen(navController: NavController) {
    var origin by remember { mutableStateOf("Gambir (GMR)") }
    var destination by remember { mutableStateOf("Bandung (BD)") }
    var selectedTrain by remember { mutableStateOf("Argo Parahyangan") }
    var selectedClass by remember { mutableStateOf("Eksekutif") }
    var selectedSchedule by remember { mutableStateOf("07:30 WIB - 10:45 WIB") }
    var passengerName by remember { mutableStateOf("") }

    val trains = listOf("Argo Parahyangan" to 150000.0, "Serayu" to 65000.0, "Lodaya" to 220000.0, "Harina" to 250000.0)
    val schedules = listOf("07:30 WIB - 10:45 WIB", "11:15 WIB - 14:30 WIB", "16:00 WIB - 19:15 WIB")
    val classes = listOf("Ekonomi", "Bisnis", "Eksekutif")

    val basePrice = trains.find { it.first == selectedTrain }?.second ?: 150000.0
    val classMultiplier = when(selectedClass) { "Eksekutif" -> 1.4; "Bisnis" -> 1.2; else -> 1.0 }
    val finalPrice = basePrice * classMultiplier
    val capitalPrice = finalPrice * 0.8

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pesan Tiket Kereta (KAI)") },
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Train, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Kereta Api Indonesia (KAI)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Perjalanan darat nyaman tepat waktu.", fontSize = 12.sp)
                    }
                }
            }

            OutlinedTextField(
                value = origin,
                onValueChange = { origin = it },
                label = { Text("Stasiun Asal") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Stasiun Tujuan") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = passengerName,
                onValueChange = { passengerName = it },
                label = { Text("Nama Penumpang (Sesuai KTP)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Pilih Kereta", fontWeight = FontWeight.Bold)
            trains.forEach { (train, price) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedTrain == train,
                        onClick = { selectedTrain = train }
                    )
                    Text("$train (Rp ${java.text.DecimalFormat("#,###").format(price)})")
                }
            }

            Text("Kelas Kereta", fontWeight = FontWeight.Bold)
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
                    val desc = "Tiket Kereta $selectedTrain ($origin -> $destination) Kelas $selectedClass Jdw: $selectedSchedule [Penumpang: ${passengerName.ifBlank { "Tamu" }}]"
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
