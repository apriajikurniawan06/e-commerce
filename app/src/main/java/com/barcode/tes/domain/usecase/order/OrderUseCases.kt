package com.barcode.tes.domain.usecase.order

import com.barcode.tes.domain.model.Order
import com.barcode.tes.domain.repository.OrderRepository

class GetOrdersUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(userId: String): Result<List<Order>> = repository.getOrders(userId)
}

class CreateOrderUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(order: Order): Result<String> = repository.createOrder(order)
}

data class OrderUseCases(
    val getOrders: GetOrdersUseCase,
    val createOrder: CreateOrderUseCase
)
