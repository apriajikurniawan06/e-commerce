package com.barcode.tes.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val id: String? = null,
    val user_id: String,
    val recipient_name: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val city: String? = null,
    val province: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val is_default: Boolean = false
)
