package com.barcode.tes.domain.repository

import com.barcode.tes.domain.model.Address

interface AddressRepository {
    suspend fun getAddresses(userId: String): Result<List<Address>>
    suspend fun getDefaultAddress(userId: String): Result<Address?>
    suspend fun addAddress(address: Address): Result<Unit>
    suspend fun updateAddress(address: Address): Result<Unit>
    suspend fun deleteAddress(addressId: String): Result<Unit>
}
