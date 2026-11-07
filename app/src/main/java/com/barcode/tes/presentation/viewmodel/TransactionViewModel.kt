package com.barcode.tes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barcode.tes.di.AppModule
import com.barcode.tes.domain.model.Order
import com.barcode.tes.domain.usecase.order.OrderUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val orderUseCases: OrderUseCases = AppModule.orderUseCases
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        val user = AppModule.authRepository.getCurrentUser()
        if (user != null) {
            fetchOrders(user.id)
        }
    }

    fun fetchOrders(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            orderUseCases.getOrders(userId).onSuccess { _orders.value = it }
            _isLoading.value = false
        }
    }

    suspend fun createOrder(order: Order): String? {
        return orderUseCases.createOrder(order).getOrNull()
    }

    suspend fun processTransaction(amount: Double, capital: Double, description: String): String? {
        val user = com.barcode.tes.di.AppModule.authRepository.getCurrentUser() ?: return null
        
        val order = com.barcode.tes.domain.model.Order(
            buyer_id = user.id, // Sementara disamakan atau buat ID pembeli anonim
            seller_id = user.id,
            description = description,
            total_amount = amount,
            capital_price = capital,
            grand_total = amount,
            payment_status = "paid",
            order_status = "completed"
        )
        
        return createOrder(order)
    }
}
