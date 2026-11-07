package com.barcode.tes.data.repository

import com.barcode.tes.data.local.dao.AddressDao
import com.barcode.tes.data.mapper.toDomain
import com.barcode.tes.data.mapper.toEntity
import com.barcode.tes.data.remote.SupabaseClient
import com.barcode.tes.domain.model.Address
import com.barcode.tes.domain.repository.AddressRepository
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.first

class AddressRepositoryImpl(private val dao: AddressDao) : AddressRepository {
    // private val dao = TesApplication.database.addressDao()

    override suspend fun getAddresses(userId: String): Result<List<Address>> {
        return try {
            val remoteResults = SupabaseClient.client.postgrest["addresses"].select {
                filter { eq("user_id", userId) }
            }.decodeList<Address>()
            
            dao.deleteAllAddresses()
            dao.insertAddresses(remoteResults.mapNotNull { it.toEntity() })
            
            Result.success(remoteResults)
        } catch (e: Exception) {
            val localResults = dao.getAllAddresses().first().map { it.toDomain() }
            if (localResults.isNotEmpty()) {
                Result.success(localResults)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun getDefaultAddress(userId: String): Result<Address?> {
        return try {
            val result = SupabaseClient.client.postgrest["addresses"].select {
                filter {
                    eq("user_id", userId)
                    eq("is_default", true)
                }
            }.decodeSingleOrNull<Address>()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addAddress(address: Address): Result<Unit> {
        return try {
            SupabaseClient.client.postgrest["addresses"].insert(address)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAddress(address: Address): Result<Unit> {
        return try {
            SupabaseClient.client.postgrest["addresses"].update(address) {
                filter { eq("id", address.id ?: "") }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAddress(addressId: String): Result<Unit> {
        return try {
            SupabaseClient.client.postgrest["addresses"].delete {
                filter { eq("id", addressId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
