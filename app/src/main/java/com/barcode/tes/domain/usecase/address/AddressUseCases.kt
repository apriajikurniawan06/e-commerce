package com.barcode.tes.domain.usecase.address

import com.barcode.tes.domain.model.Address
import com.barcode.tes.domain.repository.AddressRepository

class GetAddressesUseCase(private val repository: AddressRepository) {
    suspend operator fun invoke(userId: String): Result<List<Address>> = repository.getAddresses(userId)
}

class AddAddressUseCase(private val repository: AddressRepository) {
    suspend operator fun invoke(address: Address): Result<Unit> = repository.addAddress(address)
}

class UpdateAddressUseCase(private val repository: AddressRepository) {
    suspend operator fun invoke(address: Address): Result<Unit> = repository.updateAddress(address)
}

class DeleteAddressUseCase(private val repository: AddressRepository) {
    suspend operator fun invoke(addressId: String): Result<Unit> = repository.deleteAddress(addressId)
}

class GetDefaultAddressUseCase(private val repository: AddressRepository) {
    suspend operator fun invoke(userId: String): Result<Address?> = repository.getDefaultAddress(userId)
}

data class AddressUseCases(
    val getAddresses: GetAddressesUseCase,
    val getDefaultAddress: GetDefaultAddressUseCase,
    val addAddress: AddAddressUseCase,
    val updateAddress: UpdateAddressUseCase,
    val deleteAddress: DeleteAddressUseCase
)
