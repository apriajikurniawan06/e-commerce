package com.barcode.tes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barcode.tes.di.AppModule
import com.barcode.tes.domain.model.PpobProduct
import com.barcode.tes.domain.model.PpobTransactionResponse
import com.barcode.tes.domain.repository.PpobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PpobViewModel(
    private val repository: PpobRepository = AppModule.ppobRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<PpobProduct>>(emptyList())
    val products: StateFlow<List<PpobProduct>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _transactionStatus = MutableStateFlow<PpobTransactionResponse?>(null)
    val transactionStatus: StateFlow<PpobTransactionResponse?> = _transactionStatus

    fun fetchProducts(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getProducts(category).onSuccess {
                _products.value = it
            }
            _isLoading.value = false
        }
    }

    fun pay(productCode: String, customerNumber: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.executeTransaction(productCode, customerNumber)
                .onSuccess {
                    _transactionStatus.value = it
                    onResult(it.status == "success", it.message)
                }
                .onFailure {
                    onResult(false, it.message ?: "Terjadi kesalahan")
                }
            _isLoading.value = false
        }
    }
}
