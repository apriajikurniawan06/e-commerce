package com.barcode.tes.data.mapper

import com.barcode.tes.data.local.entity.AddressEntity
import com.barcode.tes.domain.model.Address

fun AddressEntity.toDomain(): Address {
    return Address(
        id = id,
        user_id = user_id,
        recipient_name = recipient_name,
        phone = phone,
        address = address,
        city = city,
        province = province,
        latitude = latitude,
        longitude = longitude,
        is_default = is_default
    )
}

fun Address.toEntity(): AddressEntity? {
    if (id == null) return null
    return AddressEntity(
        id = id,
        user_id = user_id,
        recipient_name = recipient_name,
        phone = phone,
        address = address,
        city = city,
        province = province,
        latitude = latitude,
        longitude = longitude,
        is_default = is_default
    )
}
