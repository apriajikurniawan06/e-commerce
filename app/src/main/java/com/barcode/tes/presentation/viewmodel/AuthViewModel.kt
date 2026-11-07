package com.barcode.tes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barcode.tes.data.remote.SupabaseClient
import com.barcode.tes.di.AppModule
import com.barcode.tes.domain.model.Profile
import com.barcode.tes.domain.usecase.auth.AuthUseCases
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authUseCases: AuthUseCases = AppModule.authUseCases
) : ViewModel() {

    private val _userProfile = MutableStateFlow<Profile?>(null)
    val userProfile: StateFlow<Profile?> = _userProfile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val currentUser: UserInfo?
        get() = AppModule.authRepository.getCurrentUser()

    init {
        fetchUserProfile()
        observeSession()
    }

    private fun observeSession() {
        viewModelScope.launch {
            SupabaseClient.client.auth.sessionStatus.collect { status ->
                if (status is SessionStatus.Authenticated) {
                    fetchUserProfile()
                }
            }
        }
    }

    fun fetchUserProfile() {
        val user = currentUser ?: return
        viewModelScope.launch {
            _isLoading.value = true
            authUseCases.getProfile(user.id)
                .onSuccess { _userProfile.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Email dan password tidak boleh kosong"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                authUseCases.login(email, password)
                    .onSuccess {
                        fetchUserProfile()
                        onSuccess()
                    }
                    .onFailure { e ->
                        _error.value = "Login gagal: ${e.message ?: "Kesalahan tidak diketahui"}"
                    }
            } catch (e: Exception) {
                _error.value = "Terjadi kesalahan sistem: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            AppModule.authRepository.loginWithGoogle()
                .onFailure { 
                    _error.value = "Login Google gagal: ${it.message}"
                    _isLoading.value = false
                }
        }
    }

    fun loginWithGoogleNative(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            AppModule.authRepository.loginWithGoogleNative(idToken)
                .onFailure {
                    _error.value = "Login Google gagal: ${it.message}"
                }
            _isLoading.value = false
        }
    }

    fun signUp(email: String, password: String, fullName: String, phone: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            authUseCases.signUp(email, password, fullName, phone)
                .onSuccess { onSuccess() }
                .onFailure { _error.value = "Daftar gagal: ${it.message}" }
            _isLoading.value = false
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authUseCases.logout()
                .onSuccess {
                    _userProfile.value = null
                    onSuccess()
                }
        }
    }

    fun updateProfile(profile: Profile, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            authUseCases.updateProfile(profile)
                .onSuccess {
                    _userProfile.value = profile
                    onSuccess()
                }
                .onFailure { _error.value = "Gagal update profil: ${it.message}" }
            _isLoading.value = false
        }
    }

    fun uploadAvatar(bytes: ByteArray, onSuccess: (String) -> Unit) {
        val user = currentUser ?: return
        viewModelScope.launch {
            _isLoading.value = true
            authUseCases.uploadAvatar(user.id, bytes)
                .onSuccess { url ->
                    val currentProfile = _userProfile.value
                    if (currentProfile != null) {
                        val updatedProfile = currentProfile.copy(avatar_url = url)
                        authUseCases.updateProfile(updatedProfile)
                            .onSuccess {
                                _userProfile.value = updatedProfile
                                onSuccess(url)
                            }
                    } else {
                        onSuccess(url)
                    }
                }
                .onFailure { _error.value = "Gagal upload avatar: ${it.message}" }
            _isLoading.value = false
        }
    }

    fun getProfileById(userId: String, onResult: (Profile?) -> Unit) {
        viewModelScope.launch {
            authUseCases.getProfile(userId)
                .onSuccess { onResult(it) }
                .onFailure { onResult(null) }
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            authUseCases.deleteAccount()
                .onSuccess { onSuccess() }
                .onFailure { _error.value = "Gagal menghapus akun: ${it.message}" }
            _isLoading.value = false
        }
    }
}
