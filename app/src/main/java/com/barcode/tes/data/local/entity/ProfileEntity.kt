package com.barcode.tes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val full_name: String?,
    val shop_name: String?,
    val phone: String?,
    val avatar_url: String?,
    val email: String?,
    val role: String,
    val is_active: Boolean
)
