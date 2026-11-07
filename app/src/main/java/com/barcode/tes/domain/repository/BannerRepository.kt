package com.barcode.tes.domain.repository

import com.barcode.tes.domain.model.Banner

interface BannerRepository {
    suspend fun getBanners(): Result<List<Banner>>
    suspend fun addBanner(banner: Banner): Result<Boolean>
    suspend fun uploadBannerImage(fileName: String, bytes: ByteArray): Result<String>
}
