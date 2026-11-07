package com.barcode.tes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val user_id: String,
    val category_id: Long?,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val unit: String,
    val weight: Double?,
    val location: String?,
    val thumbnail_url: String?,
    val rating: Double,
    val total_review: Int,
    val status: String
)
