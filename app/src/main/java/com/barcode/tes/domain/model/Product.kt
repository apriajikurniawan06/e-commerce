package com.barcode.tes.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String? = null,
    val user_id: String,
    val category_id: Long? = null,
    val name: String,
    val description: String? = null,
    val price: Double,
    val stock: Int = 0,
    val unit: String = "kg",
    val weight: Double? = null,
    val location: String? = null,
    val thumbnail_url: String? = null,
    val rating: Double = 0.0,
    val total_review: Int = 0,
    val status: String = "active",
    val images: List<ProductImage> = emptyList()
)

@Serializable
data class ProductImage(
    val id: String? = null,
    val product_id: String,
    val image_url: String,
    val is_primary: Boolean = false
)

@Serializable
data class ProductCategory(
    val id: Long? = null,
    val name: String,
    val icon_url: String? = null
)
