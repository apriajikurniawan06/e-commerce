package com.barcode.tes.data.repository

import com.barcode.tes.data.local.dao.ProfileDao
import com.barcode.tes.data.mapper.toDomain
import com.barcode.tes.data.mapper.toEntity
import com.barcode.tes.data.remote.SupabaseClient
import com.barcode.tes.domain.model.Profile
import com.barcode.tes.domain.repository.AuthRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepositoryImpl(private val dao: ProfileDao) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            SupabaseClient.client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithGoogle(): Result<Unit> {
        return try {
            // Supabase akan membuka browser untuk login Google.
            // Pastikan Redirect URL di Dashboard Supabase sudah diatur ke:
            // com.barcode.tes://login-callback
            SupabaseClient.client.auth.signInWith(Google)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithGoogleNative(idToken: String): Result<Unit> {
        return try {
            SupabaseClient.client.auth.signInWith(IDToken) {
                this.idToken = idToken
                this.provider = Google
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String, fullName: String, phone: String): Result<Unit> {
        return try {
            SupabaseClient.client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            val user = SupabaseClient.client.auth.currentUserOrNull()
            if (user != null) {
                val newProfile = Profile(
                    id = user.id,
                    full_name = fullName,
                    phone = phone,
                    email = email
                )
                SupabaseClient.client.postgrest["profiles"].upsert(newProfile)
                dao.insertProfile(newProfile.toEntity())
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val user = SupabaseClient.client.auth.currentUserOrNull()
            if (user != null) {
                dao.deleteProfile(user.id)
            }
            SupabaseClient.client.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = SupabaseClient.client.auth.currentUserOrNull()
            if (user != null) {
                dao.deleteProfile(user.id)
                SupabaseClient.client.postgrest["profiles"].delete {
                    filter { eq("id", user.id) }
                }
                SupabaseClient.client.auth.signOut()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): UserInfo? {
        return SupabaseClient.client.auth.currentUserOrNull()
    }

    override suspend fun getUserProfile(userId: String): Result<Profile> {
        return try {
            val response = SupabaseClient.client.postgrest["profiles"]
                .select {
                    filter { eq("id", userId) }
                }
            
            val profile = try {
                response.decodeSingle<Profile>()
            } catch (e: Exception) {
                // Jika profil tidak ditemukan, coba buat dari user metadata (untuk Google Login)
                val user = SupabaseClient.client.auth.currentUserOrNull()
                if (user != null && user.id == userId) {
                    val email = user.email ?: ""
                    val fullName = user.userMetadata?.get("full_name")?.toString()?.replace("\"", "")
                        ?: user.userMetadata?.get("name")?.toString()?.replace("\"", "")
                        ?: ""
                    
                    val newProfile = Profile(
                        id = userId,
                        email = email,
                        full_name = fullName
                    )
                    SupabaseClient.client.postgrest["profiles"].upsert(newProfile)
                    newProfile
                } else {
                    throw e
                }
            }
            
            // Simpan ke database lokal
            dao.insertProfile(profile.toEntity())
            
            Result.success(profile)
        } catch (e: Exception) {
            // Jika gagal (offline), ambil dari database lokal
            try {
                val localProfile = dao.getProfileById(userId).first()
                if (localProfile != null) {
                    Result.success(localProfile.toDomain())
                } else {
                    Result.failure(e)
                }
            } catch (localE: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun updateUserProfile(profile: Profile): Result<Unit> {
        return try {
            SupabaseClient.client.postgrest["profiles"].update(buildJsonObject {
                put("full_name", profile.full_name)
                put("shop_name", profile.shop_name)
                put("avatar_url", profile.avatar_url)
                put("phone", profile.phone)
                put("role", profile.role)
                put("is_active", profile.is_active)
            }) {
                filter { eq("id", profile.id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadAvatar(userId: String, bytes: ByteArray): Result<String> {
        return try {
            val fileName = "$userId/avatar.jpg"
            val bucket = SupabaseClient.client.storage.from("konten")
            bucket.upload(fileName, bytes) {
                upsert = true
            }
            val url = bucket.publicUrl(fileName)
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
