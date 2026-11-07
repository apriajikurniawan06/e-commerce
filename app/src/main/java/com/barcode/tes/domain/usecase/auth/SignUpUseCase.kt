package com.barcode.tes.domain.usecase.auth

import com.barcode.tes.domain.repository.AuthRepository

class SignUpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        password: String,
        fullName: String,
        phone: String
    ): Result<Unit> {
        return repository.signUp(email, password, fullName, phone)
    }
}
