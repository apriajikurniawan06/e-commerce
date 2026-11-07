package com.barcode.tes.data.mapper

import com.barcode.tes.data.local.entity.OrderEntity
import com.barcode.tes.domain.model.Order

fun OrderEntity.toDomain(): Order {
    return Order(
        id = id,
        buyer_id = buyer_id,
        seller_id = seller_id,
        description = description,
        total_amount = total_amount,
        capital_price = capital_price,
        grand_total = grand_total,
        payment_status = payment_status,
        order_status = order_status,
        created_at = created_at
    )
}

fun Order.toEntity(): OrderEntity? {
    if (id == null) return null
    return OrderEntity(
        id = id,
        buyer_id = buyer_id,
        seller_id = seller_id,
        description = description,
        total_amount = total_amount ?: 0.0,
        capital_price = capital_price ?: 0.0,
        grand_total = grand_total ?: 0.0,
        payment_status = payment_status,
        order_status = order_status,
        created_at = created_at
    )
}
