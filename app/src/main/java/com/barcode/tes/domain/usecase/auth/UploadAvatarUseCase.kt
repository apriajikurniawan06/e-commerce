package com.barcode.tes.domain.usecase.auth

import com.barcode.tes.domain.repository.AuthRepository

class UploadAvatarUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(userId: String, bytes: ByteArray): Result<String> {
        return repository.uploadAvatar(userId, bytes)
    }
}
