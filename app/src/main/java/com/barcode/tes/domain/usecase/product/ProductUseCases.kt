package com.barcode.tes.domain.usecase.product

import com.barcode.tes.domain.model.Product
import com.barcode.tes.domain.model.ProductCategory
import com.barcode.tes.domain.repository.ProductRepository

class GetCategoriesUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(): Result<List<ProductCategory>> = repository.getCategories()
}

class AddProductUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(product: Product): Result<Boolean> = repository.addProduct(product)
}

class UpdateProductUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(product: Product): Result<Boolean> = repository.updateProduct(product)
}

class DeleteProductUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(productId: String): Result<Boolean> = repository.deleteProduct(productId)
}

class UploadImageUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(fileName: String, bytes: ByteArray): Result<String> = repository.uploadImage(fileName, bytes)
}

data class ProductUseCases(
    val getProducts: GetProductsUseCase,
    val getCategories: GetCategoriesUseCase,
    val addProduct: AddProductUseCase,
    val updateProduct: UpdateProductUseCase,
    val deleteProduct: DeleteProductUseCase,
    val uploadImage: UploadImageUseCase
)
