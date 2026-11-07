package com.barcode.tes.data.repository

import com.barcode.tes.data.local.dao.OrderDao
import com.barcode.tes.data.mapper.toDomain
import com.barcode.tes.data.mapper.toEntity
import com.barcode.tes.data.remote.SupabaseClient
import com.barcode.tes.domain.model.Order
import com.barcode.tes.domain.repository.OrderRepository
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.first

class OrderRepositoryImpl(private val dao: OrderDao) : OrderRepository {

    override suspend fun getOrders(userId: String): Result<List<Order>> {
        return try {
            val remoteResults = SupabaseClient.client.postgrest["orders"].select {
                filter {
                    or {
                        eq("buyer_id", userId)
                        eq("seller_id", userId)
                    }
                }
            }.decodeList<Order>()
            
            dao.deleteAllOrders()
            dao.insertOrders(remoteResults.mapNotNull { it.toEntity() })
            
            Result.success(remoteResults)
        } catch (e: Exception) {
            val localResults = dao.getAllOrders().first().map { it.toDomain() }
            if (localResults.isNotEmpty()) {
                Result.success(localResults)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun createOrder(order: Order): Result<String> {
        return try {
            // Hilangkan ID jika null agar Supabase men-generate UUID otomatis
            val orderToInsert = if (order.id == null) order else order.copy(id = null)
            
            val response = SupabaseClient.client.postgrest["orders"].insert(orderToInsert) {
                select()
            }.decodeSingle<Order>()
            
            // Simpan ke cache lokal juga
            dao.insertOrders(listOf(response.toEntity()).mapNotNull { it })
            
            Result.success(response.id ?: "")
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
