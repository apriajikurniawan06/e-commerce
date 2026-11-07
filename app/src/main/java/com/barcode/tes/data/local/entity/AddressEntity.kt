package com.barcode.tes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey val id: String,
    val user_id: String,
    val recipient_name: String?,
    val phone: String?,
    val address: String?,
    val city: String?,
    val province: String?,
    val latitude: Double?,
    val longitude: Double?,
    val is_default: Boolean
)
