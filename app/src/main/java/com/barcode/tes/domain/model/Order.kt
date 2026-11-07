package com.barcode.tes.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String? = null,
    val buyer_id: String,
    val seller_id: String,
    val address_id: String? = null,
    val description: String? = null,
    val total_amount: Double? = 0.0,
    val capital_price: Double? = 0.0,
    val shipping_cost: Double? = 0.0,
    val grand_total: Double? = 0.0,
    val payment_status: String = "pending",
    val order_status: String = "waiting",
    val created_at: String? = null
)

@Serializable
data class OrderItem(
    val id: String? = null,
    val order_id: String,
    val product_id: String,
    val qty: Int,
    val price: Double,
    val subtotal: Double
)
