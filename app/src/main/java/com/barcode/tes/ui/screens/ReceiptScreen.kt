package com.barcode.tes.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.compose.foundation.Canvas as ComposeCanvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.barcode.tes.domain.model.Order
import com.barcode.tes.presentation.viewmodel.TransactionViewModel
import com.barcode.tes.ui.navigation.Screen
import androidx.compose.ui.tooling.preview.Preview
import com.barcode.tes.ui.theme.TesTheme
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReceiptScreen(transactionId: String, navController: NavController, viewModel: TransactionViewModel = viewModel()) {
    val orders by viewModel.orders.collectAsState()
    val transaction = remember(orders, transactionId) { orders.find { it.id == transactionId } }
    val context = LocalContext.current

    ReceiptContent(
        transactionId = transactionId,
        transaction = transaction,
        onGeneratePDF = { order -> generatePDF(context, order) },
        onSaveImage = { order -> saveReceiptAsImage(context, order) },
        onShareImage = { order -> shareReceiptAsImage(context, order) },
        onBackToHome = {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    )
}

@Composable
fun ReceiptContent(
    transactionId: String,
    transaction: Order?,
    onGeneratePDF: (Order) -> Unit,
    onSaveImage: (Order) -> Unit,
    onShareImage: (Order) -> Unit,
    onBackToHome: () -> Unit
) {
    if (transaction == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                
                // Animasi Checkmark
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(70.dp)
                )
                
                Text(
                    "Pembayaran Berhasil",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "ID: ${transactionId.take(12)}...",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Desain Struk Modern
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "BISNIZKU",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                        Text(
                            "Bukti Transaksi Digital",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Garis Putus-putus
                        DashedDivider()
                        
                        Spacer(modifier = Modifier.height(20.dp))

                        ReceiptDetailRow("Produk", transaction.description ?: "-", isTitle = true)
                        ReceiptDetailRow("Tanggal", SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()))
                        ReceiptDetailRow("Metode", "E-Wallet / VA")
                        ReceiptDetailRow("Status", "SUKSES", valueColor = Color(0xFF2E7D32))
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        DashedDivider()
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Bayar", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                            Text(
                                "Rp ${java.text.DecimalFormat("#,###").format(transaction.grand_total ?: 0.0)}",
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            "Terima kasih telah menggunakan layanan kami!",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Tombol Aksi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onGeneratePDF(transaction) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Download, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PDF", fontSize = 11.sp)
                    }
                    
                    Button(
                        onClick = { onSaveImage(transaction) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Image, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("IMG", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { onShareImage(transaction) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SHARE", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onBackToHome,
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFFF))
                ) {
                    Icon(Icons.Default.Home, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("KEMBALI KE BERANDA", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ReceiptDetailRow(label: String, value: String, isTitle: Boolean = false, valueColor: Color? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = if (isTitle) FontWeight.Bold else FontWeight.Medium,
            color = valueColor ?: MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

@Composable
fun DashedDivider(color: Color = Color.LightGray) {
    ComposeCanvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
    }
}

fun generatePDF(context: Context, order: Order) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(400, 700, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas: Canvas = page.canvas
    val paint = Paint()

    // Header
    paint.textAlign = Paint.Align.CENTER
    paint.textSize = 24f
    paint.isFakeBoldText = true
    canvas.drawText("BISNIZKU", 200f, 60f, paint)
    
    paint.textSize = 12f
    paint.isFakeBoldText = false
    paint.color = android.graphics.Color.GRAY
    canvas.drawText("Digital Receipt", 200f, 85f, paint)

    paint.color = android.graphics.Color.LTGRAY
    canvas.drawLine(40f, 110f, 360f, 110f, paint)

    // Details
    paint.color = android.graphics.Color.BLACK
    paint.textAlign = Paint.Align.LEFT
    paint.textSize = 14f
    var y = 150f
    val leftX = 50f
    val rightX = 350f

    val details = listOf(
        "Transaction ID" to (order.id?.take(15) ?: "-"),
        "Product" to (order.description ?: "-"),
        "Date" to SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
        "Status" to "SUCCESS"
    )

    details.forEach { (label, value) ->
        paint.color = android.graphics.Color.GRAY
        canvas.drawText(label, leftX, y, paint)
        paint.color = android.graphics.Color.BLACK
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(value, rightX, y, paint)
        paint.textAlign = Paint.Align.LEFT
        y += 30f
    }

    y += 20f
    canvas.drawLine(40f, y, 360f, y, paint)
    y += 40f

    paint.isFakeBoldText = true
    paint.textSize = 18f
    canvas.drawText("TOTAL", leftX, y, paint)
    paint.textAlign = Paint.Align.RIGHT
    canvas.drawText("Rp ${java.text.DecimalFormat("#,###").format(order.grand_total ?: 0.0)}", rightX, y, paint)

    y += 80f
    paint.textAlign = Paint.Align.CENTER
    paint.textSize = 12f
    paint.isFakeBoldText = false
    paint.color = android.graphics.Color.GRAY
    canvas.drawText("Thank you for your transaction!", 200f, y, paint)

    pdfDocument.finishPage(page)

    val fileName = "Struk_${System.currentTimeMillis()}.pdf"
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

    try {
        pdfDocument.writeTo(FileOutputStream(file))
        Toast.makeText(context, "PDF Berhasil disimpan", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
    }
    pdfDocument.close()
}

fun saveReceiptAsImage(context: Context, order: Order) {
    val width = 600
    val height = 900
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()

    // Background Putih Bersih
    canvas.drawColor(android.graphics.Color.WHITE)

    // Draw Receipt Border
    paint.color = android.graphics.Color.BLACK
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 2f
    canvas.drawRect(20f, 20f, width - 20f, height - 20f, paint)

    paint.style = Paint.Style.FILL
    paint.textAlign = Paint.Align.CENTER
    paint.textSize = 40f
    paint.isFakeBoldText = true
    canvas.drawText("BISNIZKU", width / 2f, 100f, paint)

    paint.textSize = 20f
    paint.isFakeBoldText = false
    paint.color = android.graphics.Color.GRAY
    canvas.drawText("STRUK PEMBAYARAN RESMI", width / 2f, 140f, paint)

    paint.color = android.graphics.Color.BLACK
    var y = 220f
    val leftX = 60f
    val rightX = width - 60f

    val rows = listOf(
        "ID Transaksi" to (order.id?.take(18) ?: "-"),
        "Item" to (order.description ?: "-"),
        "Waktu" to SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date()),
        "Metode" to "Digital Pay",
        "Status" to "BERHASIL"
    )

    rows.forEach { (label, value) ->
        paint.textAlign = Paint.Align.LEFT
        paint.color = android.graphics.Color.DKGRAY
        canvas.drawText(label, leftX, y, paint)
        paint.textAlign = Paint.Align.RIGHT
        paint.color = android.graphics.Color.BLACK
        canvas.drawText(value, rightX, y, paint)
        y += 50f
    }

    y += 30f
    paint.strokeWidth = 1f
    canvas.drawLine(leftX, y, rightX, y, paint)
    y += 70f

    paint.textAlign = Paint.Align.LEFT
    paint.textSize = 35f
    paint.isFakeBoldText = true
    canvas.drawText("TOTAL", leftX, y, paint)
    paint.textAlign = Paint.Align.RIGHT
    canvas.drawText("Rp ${java.text.DecimalFormat("#,###").format(order.grand_total ?: 0.0)}", rightX, y, paint)

    y += 150f
    paint.textAlign = Paint.Align.CENTER
    paint.textSize = 18f
    paint.isFakeBoldText = false
    paint.color = android.graphics.Color.GRAY
    canvas.drawText("Simpan struk ini sebagai bukti pembayaran sah.", width / 2f, y, paint)

    val fileName = "Struk_${System.currentTimeMillis()}.png"
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)

    try {
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()
        Toast.makeText(context, "Gambar Struk tersimpan di Galeri", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Gagal simpan gambar: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun shareReceiptAsImage(context: Context, order: Order) {
    val width = 600
    val height = 900
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()

    canvas.drawColor(android.graphics.Color.WHITE)
    paint.color = android.graphics.Color.BLACK
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 2f
    canvas.drawRect(20f, 20f, width - 20f, height - 20f, paint)

    paint.style = Paint.Style.FILL
    paint.textAlign = Paint.Align.CENTER
    paint.textSize = 40f
    paint.isFakeBoldText = true
    canvas.drawText("BISNIZKU", width / 2f, 100f, paint)

    paint.textSize = 20f
    paint.isFakeBoldText = false
    paint.color = android.graphics.Color.GRAY
    canvas.drawText("STRUK PEMBAYARAN DIGITAL", width / 2f, 140f, paint)

    paint.color = android.graphics.Color.BLACK
    var y = 220f
    val leftX = 60f
    val rightX = width - 60f

    val rows = listOf(
        "ID Transaksi" to (order.id?.take(18) ?: "-"),
        "Item" to (order.description ?: "-"),
        "Waktu" to SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date()),
        "Status" to "BERHASIL"
    )

    rows.forEach { (label, value) ->
        paint.textAlign = Paint.Align.LEFT
        paint.color = android.graphics.Color.DKGRAY
        canvas.drawText(label, leftX, y, paint)
        paint.textAlign = Paint.Align.RIGHT
        paint.color = android.graphics.Color.BLACK
        canvas.drawText(value, rightX, y, paint)
        y += 50f
    }

    y += 30f
    canvas.drawLine(leftX, y, rightX, y, paint)
    y += 70f

    paint.textAlign = Paint.Align.LEFT
    paint.textSize = 35f
    paint.isFakeBoldText = true
    canvas.drawText("TOTAL", leftX, y, paint)
    paint.textAlign = Paint.Align.RIGHT
    canvas.drawText("Rp ${java.text.DecimalFormat("#,###").format(order.grand_total ?: 0.0)}", rightX, y, paint)

    val fileName = "Share_Struk_${System.currentTimeMillis()}.png"
    val file = File(context.cacheDir, fileName)

    try {
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan Struk Melalui"))
    } catch (e: Exception) {
        Toast.makeText(context, "Gagal membagikan: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true)
@Composable
fun ReceiptScreenPreview() {
    val sampleOrder = Order(
        id = "TRX1234567890",
        buyer_id = "user1",
        seller_id = "user2",
        description = "Paket Internet 10GB",
        grand_total = 50000.0,
        created_at = "2023-10-27 10:00:00"
    )
    TesTheme {
        ReceiptContent(
            transactionId = "TRX1234567890",
            transaction = sampleOrder,
            onGeneratePDF = {},
            onSaveImage = {},
            onShareImage = {},
            onBackToHome = {}
        )
    }
}
