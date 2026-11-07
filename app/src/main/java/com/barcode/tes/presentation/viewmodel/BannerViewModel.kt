package com.barcode.tes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barcode.tes.di.AppModule
import com.barcode.tes.domain.model.Banner
import com.barcode.tes.domain.usecase.banner.BannerUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BannerViewModel(
    private val bannerUseCases: BannerUseCases = AppModule.bannerUseCases
) : ViewModel() {

    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchBanners()
    }

    fun fetchBanners() {
        viewModelScope.launch {
            _isLoading.value = true
            bannerUseCases.getBanners().onSuccess { _banners.value = it }
            _isLoading.value = false
        }
    }

    suspend fun addBanner(banner: Banner): Boolean {
        val result = bannerUseCases.addBanner(banner).getOrDefault(false)
        if (result) fetchBanners()
        return result
    }

    suspend fun uploadBannerImage(fileName: String, bytes: ByteArray): String? {
        return bannerUseCases.uploadBannerImage(fileName, bytes).getOrNull()
    }
}
