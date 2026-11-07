package com.barcode.tes.domain.usecase.auth

import com.barcode.tes.domain.model.Profile
import com.barcode.tes.domain.repository.AuthRepository

class UpdateProfileUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(profile: Profile): Result<Unit> {
        return repository.updateUserProfile(profile)
    }
}
