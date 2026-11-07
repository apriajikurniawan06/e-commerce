package com.barcode.tes.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val full_name: String? = null,
    val shop_name: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val avatar_url: String? = null,
    val role: String = "user",
    val is_active: Boolean = true,
    val created_at: String? = null,
    val updated_at: String? = null
)
