package com.barcode.tes.domain.repository

import com.barcode.tes.domain.model.PpobProduct
import com.barcode.tes.domain.model.PpobTransactionResponse

interface PpobRepository {
    suspend fun getProducts(category: String): Result<List<PpobProduct>>
    suspend fun checkPrice(productCode: String): Result<Double>
    suspend fun executeTransaction(
        productCode: String,
        customerNumber: String
    ): Result<PpobTransactionResponse>
}
