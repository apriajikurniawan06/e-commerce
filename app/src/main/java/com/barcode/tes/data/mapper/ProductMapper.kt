package com.barcode.tes.data.mapper

import com.barcode.tes.data.local.entity.CategoryEntity
import com.barcode.tes.data.local.entity.ProductEntity
import com.barcode.tes.domain.model.Product
import com.barcode.tes.domain.model.ProductCategory

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        user_id = user_id,
        category_id = category_id,
        name = name,
        description = description,
        price = price,
        stock = stock,
        unit = unit,
        weight = weight,
        location = location,
        thumbnail_url = thumbnail_url,
        rating = rating,
        total_review = total_review,
        status = status
    )
}

fun Product.toEntity(): ProductEntity? {
    if (id == null) return null
    return ProductEntity(
        id = id,
        user_id = user_id,
        category_id = category_id,
        name = name,
        description = description,
        price = price,
        stock = stock,
        unit = unit,
        weight = weight,
        location = location,
        thumbnail_url = thumbnail_url,
        rating = rating,
        total_review = total_review,
        status = status
    )
}

fun CategoryEntity.toDomain(): ProductCategory {
    return ProductCategory(
        id = id,
        name = name,
        icon_url = icon_url
    )
}

fun ProductCategory.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id ?: 0L,
        name = name,
        icon_url = icon_url
    )
}
