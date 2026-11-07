package com.barcode.tes.data.repository

import com.barcode.tes.data.local.dao.BannerDao
import com.barcode.tes.data.mapper.toDomain
import com.barcode.tes.data.mapper.toEntity
import com.barcode.tes.data.remote.SupabaseClient
import com.barcode.tes.domain.model.Banner
import com.barcode.tes.domain.repository.BannerRepository
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.first

class BannerRepositoryImpl(private val dao: BannerDao) : BannerRepository {

    override suspend fun getBanners(): Result<List<Banner>> {
        return try {
            val remoteResults = SupabaseClient.client.postgrest["banners"].select().decodeList<Banner>()
            
            dao.deleteAllBanners()
            dao.insertBanners(remoteResults.mapNotNull { it.toEntity() })
            
            Result.success(remoteResults)
        } catch (e: Exception) {
            val localResults = dao.getActiveBanners().first().map { it.toDomain() }
            if (localResults.isNotEmpty()) {
                Result.success(localResults)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun addBanner(banner: Banner): Result<Boolean> {
        return try {
            SupabaseClient.client.postgrest["banners"].insert(banner)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadBannerImage(fileName: String, bytes: ByteArray): Result<String> {
        return try {
            val bucket = SupabaseClient.client.storage["banners"]
            bucket.upload(fileName, bytes) { upsert = true }
            Result.success(bucket.publicUrl(fileName))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
