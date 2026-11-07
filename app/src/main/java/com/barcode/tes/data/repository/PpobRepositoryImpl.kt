package com.barcode.tes.data.repository

import com.barcode.tes.domain.model.PpobProduct
import com.barcode.tes.domain.model.PpobTransactionResponse
import com.barcode.tes.domain.repository.PpobRepository
import io.ktor.client.*
import java.security.MessageDigest

class PpobRepositoryImpl(private val client: HttpClient) : PpobRepository {

    // Mode Simulasi / Demo PPOB (Aman & Berfungsi tanpa credential API eksternal asli)
    private val USERNAME = "demo_user"
    private val API_KEY = "demo_api_key_secure"

    // Menggunakan SHA-256 (lebih kuat dari MD5) untuk hashing signature jika diperlukan
    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    override suspend fun getProducts(category: String): Result<List<PpobProduct>> {
        return try {
            // Contoh pembuatan signature menggunakan SHA-256 (lebih kuat dari MD5)
            val signatureInput = USERNAME + API_KEY + "pricelist"
            val secureSign = sha256(signatureInput)

            // Data Produk PPOB Simulasi (Demo Mode yang berfungsi penuh)
            val dummyProducts = listOf(
                PpobProduct("telkomsel5", "Telkomsel 5rb", 5300.0, 5200.0, "Pulsa Telkomsel 5.000", "pulsa"),
                PpobProduct("telkomsel10", "Telkomsel 10rb", 10300.0, 10200.0, "Pulsa Telkomsel 10.000", "pulsa"),
                PpobProduct("telkomsel20", "Telkomsel 20rb", 20300.0, 20200.0, "Pulsa Telkomsel 20.000", "pulsa"),
                PpobProduct("tri1gb", "Tri 1GB 3 Hari", 15000.0, 14000.0, "Paket Data Tri 1GB", "data"),
                PpobProduct("tri5gb", "Tri 5GB 30 Hari", 60000.0, 58000.0, "Paket Data Tri 5GB", "data"),
                PpobProduct("isat2gb", "Indosat 2GB 30 Hari", 25000.0, 23000.0, "Paket Data Indosat 2GB", "data"),
                PpobProduct("xl5gb", "XL 5GB 30 Hari", 45000.0, 43000.0, "Paket Data XL 5GB", "data")
            )

            val filtered = dummyProducts.filter { it.category.equals(category, ignoreCase = true) }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkPrice(productCode: String): Result<Double> {
        return try {
            val priceMap = mapOf(
                "telkomsel5" to 5300.0,
                "telkomsel10" to 10300.0,
                "telkomsel20" to 20300.0,
                "tri1gb" to 15000.0,
                "tri5gb" to 60000.0,
                "isat2gb" to 25000.0,
                "xl5gb" to 45000.0
            )
            val price = priceMap[productCode] ?: 10000.0
            Result.success(price)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun executeTransaction(
        productCode: String,
        customerNumber: String
    ): Result<PpobTransactionResponse> {
        return try {
            val refId = "REF-DEMO-${System.currentTimeMillis()}"
            
            // Pembuatan sign transaksi dengan SHA-256 (lebih kuat dari MD5)
            val signatureInput = USERNAME + API_KEY + refId
            val secureSign = sha256(signatureInput)

            if (customerNumber.length < 4) {
                return Result.failure(IllegalArgumentException("Nomor pelanggan tidak valid"))
            }

            // Simulasi transaksi PPOB Berhasil
            Result.success(
                PpobTransactionResponse(
                    status = "success",
                    message = "Transaksi $productCode ke $customerNumber Berhasil (Demo Mode - SHA256 Sign: ${secureSign.take(8)}...)",
                    sn = "SN-DEMO-${System.currentTimeMillis()}"
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
