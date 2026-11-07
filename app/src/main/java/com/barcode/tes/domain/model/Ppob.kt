package com.barcode.tes.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PpobProduct(
    val code: String,
    val name: String,
    val price: Double,
    val capital_price: Double,
    val description: String? = null,
    val category: String,
    val provider: String? = null,
    val status: String = "active"
)

@Serializable
data class PpobTransactionRequest(
    val productCode: String,
    val customerNumber: String,
    val refId: String? = null
)

@Serializable
data class PpobTransactionResponse(
    val status: String, // success, pending, failed
    val message: String,
    val sn: String? = null,
    val refId: String? = null,
    val balance: Double? = null
)
