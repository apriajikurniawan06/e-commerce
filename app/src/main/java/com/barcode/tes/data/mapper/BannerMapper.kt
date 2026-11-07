package com.barcode.tes.data.mapper

import com.barcode.tes.data.local.entity.BannerEntity
import com.barcode.tes.domain.model.Banner

fun BannerEntity.toDomain(): Banner {
    return Banner(
        id = id,
        user_id = user_id,
        title = title,
        image_url = image_url,
        link_url = link_url,
        is_active = is_active
    )
}

fun Banner.toEntity(): BannerEntity? {
    if (id == null) return null
    return BannerEntity(
        id = id,
        user_id = user_id,
        title = title,
        image_url = image_url,
        link_url = link_url,
        is_active = is_active
    )
}
