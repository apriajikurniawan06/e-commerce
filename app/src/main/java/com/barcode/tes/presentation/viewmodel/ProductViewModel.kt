package com.barcode.tes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barcode.tes.di.AppModule
import com.barcode.tes.domain.model.Product
import com.barcode.tes.domain.model.ProductCategory
import com.barcode.tes.domain.usecase.product.ProductUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(
    private val productUseCases: ProductUseCases = AppModule.productUseCases
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _categories = MutableStateFlow<List<ProductCategory>>(emptyList())
    val categories: StateFlow<List<ProductCategory>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchProducts()
        fetchCategories()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            productUseCases.getProducts().onSuccess { _products.value = it }
            _isLoading.value = false
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            productUseCases.getCategories().onSuccess { _categories.value = it }
        }
    }

    suspend fun addProduct(product: Product): Boolean {
        val result = productUseCases.addProduct(product).getOrDefault(false)
        if (result) fetchProducts()
        return result
    }

    suspend fun updateProduct(product: Product): Boolean {
        return productUseCases.updateProduct(product).getOrDefault(false)
    }

    suspend fun deleteProduct(productId: String): Boolean {
        val result = productUseCases.deleteProduct(productId).getOrDefault(false)
        if (result) fetchProducts()
        return result
    }

    suspend fun uploadImage(fileName: String, bytes: ByteArray): String? {
        return productUseCases.uploadImage(fileName, bytes).getOrNull()
    }
}
