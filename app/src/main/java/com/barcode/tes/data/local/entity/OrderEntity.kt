package com.barcode.tes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val buyer_id: String,
    val seller_id: String,
    val description: String?,
    val total_amount: Double,
    val capital_price: Double,
    val grand_total: Double,
    val payment_status: String,
    val order_status: String,
    val created_at: String?
)
