package com.barcode.tes.domain.usecase.banner

import com.barcode.tes.domain.model.Banner
import com.barcode.tes.domain.repository.BannerRepository

class GetBannersUseCase(private val repository: BannerRepository) {
    suspend operator fun invoke(): Result<List<Banner>> = repository.getBanners()
}

class AddBannerUseCase(private val repository: BannerRepository) {
    suspend operator fun invoke(banner: Banner): Result<Boolean> = repository.addBanner(banner)
}

class UploadBannerImageUseCase(private val repository: BannerRepository) {
    suspend operator fun invoke(fileName: String, bytes: ByteArray): Result<String> = repository.uploadBannerImage(fileName, bytes)
}

data class BannerUseCases(
    val getBanners: GetBannersUseCase,
    val addBanner: AddBannerUseCase,
    val uploadBannerImage: UploadBannerImageUseCase
)
