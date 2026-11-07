package com.barcode.tes.data.mapper

import com.barcode.tes.data.local.entity.ProfileEntity
import com.barcode.tes.domain.model.Profile

fun ProfileEntity.toDomain(): Profile {
    return Profile(
        id = id,
        full_name = full_name,
        shop_name = shop_name,
        phone = phone,
        avatar_url = avatar_url,
        email = email,
        role = role,
        is_active = is_active
    )
}

fun Profile.toEntity(): ProfileEntity {
    return ProfileEntity(
        id = id ?: "",
        full_name = full_name,
        shop_name = shop_name,
        phone = phone,
        avatar_url = avatar_url,
        email = email,
        role = role,
        is_active = is_active
    )
}
