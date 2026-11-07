package com.barcode.tes.domain.usecase.auth

import com.barcode.tes.domain.model.Profile
import com.barcode.tes.domain.repository.AuthRepository

class GetProfileUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(userId: String): Result<Profile> {
        return repository.getUserProfile(userId)
    }
}
