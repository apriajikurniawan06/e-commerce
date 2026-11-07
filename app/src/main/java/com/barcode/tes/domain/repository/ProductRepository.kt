package com.barcode.tes.domain.repository

import com.barcode.tes.domain.model.Product
import com.barcode.tes.domain.model.ProductCategory

interface ProductRepository {
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getCategories(): Result<List<ProductCategory>>
    suspend fun addProduct(product: Product): Result<Boolean>
    suspend fun updateProduct(product: Product): Result<Boolean>
    suspend fun deleteProduct(productId: String): Result<Boolean>
    suspend fun uploadImage(fileName: String, byteArray: ByteArray): Result<String>
}
