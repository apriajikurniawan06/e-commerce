package com.barcode.tes.data.repository

import com.barcode.tes.data.local.dao.CategoryDao
import com.barcode.tes.data.local.dao.ProductDao
import com.barcode.tes.data.mapper.toDomain
import com.barcode.tes.data.mapper.toEntity
import com.barcode.tes.data.remote.SupabaseClient
import com.barcode.tes.domain.model.Product
import com.barcode.tes.domain.model.ProductCategory
import com.barcode.tes.domain.repository.ProductRepository
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.first

class ProductRepositoryImpl(
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao
) : ProductRepository {

    override suspend fun getProducts(): Result<List<Product>> {
        return try {
            // Try fetch from remote
            val remoteResults = SupabaseClient.client.postgrest["products"].select().decodeList<Product>()
            
            // Update local cache
            productDao.deleteAllProducts()
            productDao.insertProducts(remoteResults.mapNotNull { it.toEntity() })
            
            Result.success(remoteResults)
        } catch (e: Exception) {
            // Fallback to local cache
            val localResults = productDao.getAllProducts().first().map { it.toDomain() }
            if (localResults.isNotEmpty()) {
                Result.success(localResults)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getCategories(): Result<List<ProductCategory>> {
        return try {
            val remoteResults = SupabaseClient.client.postgrest["product_categories"].select().decodeList<ProductCategory>()
            
            // Update local cache
            categoryDao.deleteAllCategories()
            categoryDao.insertCategories(remoteResults.map { it.toEntity() })
            
            Result.success(remoteResults)
        } catch (e: Exception) {
            // Fallback to local cache
            val localResults = categoryDao.getAllCategories().first().map { it.toDomain() }
            if (localResults.isNotEmpty()) {
                Result.success(localResults)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun addProduct(product: Product): Result<Boolean> {
        return try {
            SupabaseClient.client.postgrest["products"].insert(product)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProduct(product: Product): Result<Boolean> {
        return try {
            SupabaseClient.client.postgrest["products"].update(product) {
                filter { eq("id", product.id ?: "") }
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProduct(productId: String): Result<Boolean> {
        return try {
            SupabaseClient.client.postgrest["products"].delete {
                filter { eq("id", productId) }
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadImage(fileName: String, byteArray: ByteArray): Result<String> {
        return try {
            val bucket = SupabaseClient.client.storage[SupabaseClient.PRODUCT_IMAGES_BUCKET]
            bucket.upload(fileName, byteArray) { upsert = true }
            Result.success(bucket.publicUrl(fileName))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
