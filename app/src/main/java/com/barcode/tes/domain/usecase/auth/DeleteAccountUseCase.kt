package com.barcode.tes.domain.usecase.auth

import com.barcode.tes.domain.repository.AuthRepository

class DeleteAccountUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.deleteAccount()
}
