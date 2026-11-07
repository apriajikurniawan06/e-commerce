package com.barcode.tes.domain.repository

import com.barcode.tes.domain.model.Profile
import io.github.jan.supabase.auth.user.UserInfo

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun loginWithGoogle(): Result<Unit>
    suspend fun loginWithGoogleNative(idToken: String): Result<Unit>
    suspend fun signUp(email: String, password: String, fullName: String, phone: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    fun getCurrentUser(): UserInfo?
    suspend fun getUserProfile(userId: String): Result<Profile>
    suspend fun updateUserProfile(profile: Profile): Result<Unit>
    suspend fun uploadAvatar(userId: String, bytes: ByteArray): Result<String>
}
