package com.barcode.tes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "banners")
data class BannerEntity(
    @PrimaryKey val id: String,
    val user_id: String,
    val title: String?,
    val image_url: String,
    val link_url: String?,
    val is_active: Boolean
)
