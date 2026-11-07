package com.barcode.tes.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Banner(
    val id: String? = null,
    val user_id: String,
    val title: String? = null,
    val image_url: String,
    val link_url: String? = null,
    val is_active: Boolean = true
)
