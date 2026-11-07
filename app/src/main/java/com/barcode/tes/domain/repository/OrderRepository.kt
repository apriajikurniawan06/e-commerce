package com.barcode.tes.domain.repository

import com.barcode.tes.domain.model.Order

interface OrderRepository {
    suspend fun getOrders(userId: String): Result<List<Order>>
    suspend fun createOrder(order: Order): Result<String>
}
