package com.barcode.tes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barcode.tes.di.AppModule
import com.barcode.tes.domain.model.Address
import com.barcode.tes.domain.usecase.address.AddressUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddressViewModel(
    private val addressUseCases: AddressUseCases = AppModule.addressUseCases
) : ViewModel() {

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchAddresses(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            addressUseCases.getAddresses(userId).onSuccess { _addresses.value = it }
            _isLoading.value = false
        }
    }

    fun addAddress(address: Address, onSuccess: () -> Unit) {
        viewModelScope.launch {
            addressUseCases.addAddress(address).onSuccess { onSuccess() }
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            addressUseCases.deleteAddress(addressId)
        }
    }

    fun fetchDefaultAddress(userId: String, onResult: (Address?) -> Unit) {
        viewModelScope.launch {
            addressUseCases.getDefaultAddress(userId)
                .onSuccess { onResult(it) }
                .onFailure { onResult(null) }
        }
    }
}
