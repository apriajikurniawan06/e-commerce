package com.barcode.tes.domain.usecase.product

import com.barcode.tes.domain.model.Product
import com.barcode.tes.domain.repository.ProductRepository

class GetProductsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): Result<List<Product>> {
        return repository.getProducts()
    }
}
