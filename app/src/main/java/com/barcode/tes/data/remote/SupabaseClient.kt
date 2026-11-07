package com.barcode.tes.data.remote

import com.barcode.tes.BuildConfig
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpRequestRetry
import kotlin.time.Duration.Companion.seconds

object SupabaseClient {
    @OptIn(SupabaseInternal::class)
    val URL = BuildConfig.SUPABASE_URL.ifBlank { "https://trdzsmwzgdeaxtpmqrwh.supabase.co" }
    @OptIn(SupabaseInternal::class)
    val ANON_KEY = BuildConfig.SUPABASE_ANON_KEY.ifBlank { "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRyZHpzbXd6Z2RlYXh0cG1xcndoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTI2ODUwNzksImV4cCI6MjEwODI2MTA3OX0.92iVSnOqqDiuCuyeXUPWMm3B4hxHu1vAVJ1CjkCpfJc\n" }

    const val PRODUCT_IMAGES_BUCKET = "konten"

    fun imageUrl(imageUrlOrPath: String?, defaultBucket: String = PRODUCT_IMAGES_BUCKET): String? {
        val imageUrl = imageUrlOrPath?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        
        if (imageUrl.startsWith("content://", ignoreCase = true) ||
            imageUrl.startsWith("file://", ignoreCase = true)
        ) {
            return imageUrl
        }

        if (imageUrl.contains("/storage/v1/object/public/")) {
            val publicMarker = "/storage/v1/object/public/"
            val index = imageUrl.indexOf(publicMarker)
            if (index != -1) {
                val pathAfterPublic = imageUrl.substring(index + publicMarker.length)
                val parts = pathAfterPublic.split("/", limit = 2)
                if (parts.size == 2) {
                    val bucketName = parts[0]
                    val objectPath = parts[1]
                    try {
                        return client.storage[bucketName].publicUrl(objectPath)
                    } catch (_: Exception) {

                    }
                }
            }
        }

        if (imageUrl.startsWith("https://", ignoreCase = true) ||
            imageUrl.startsWith("http://", ignoreCase = true)
        ) {
            if (imageUrl.contains("supabase.co/storage/")) {
                val storageIndex = imageUrl.indexOf("/storage/")
                if (storageIndex != -1) {
                    return URL.removeSuffix("/") + imageUrl.substring(storageIndex)
                }
            }
            return imageUrl
        }

        val objectPath = imageUrl
            .removePrefix("$defaultBucket/")
            .removePrefix("konten/")
            .removePrefix("banners/")
            .trimStart('/')
        
        return try {
            client.storage[defaultBucket].publicUrl(objectPath)
        } catch (e: Exception) {
            "${URL.removeSuffix("/")}/storage/v1/object/public/$defaultBucket/$objectPath"
        }
    }

    fun productImageUrl(imageUrlOrPath: String?): String? = imageUrl(imageUrlOrPath, PRODUCT_IMAGES_BUCKET)
    fun bannerImageUrl(imageUrlOrPath: String?): String? = imageUrl(imageUrlOrPath, "banners")

    val client = createSupabaseClient(URL, ANON_KEY) {
        install(Auth) {
            scheme = "com.barcode.tes"
            host = "login-callback"
        }
        install(Postgrest)
        install(Storage)
        
        @OptIn(SupabaseInternal::class)
        httpConfig {
            // GLOBAL TIMEOUT (Anti-DDoS Client Side)
            install(HttpTimeout) {
                requestTimeoutMillis = 15.seconds.inWholeMilliseconds
                connectTimeoutMillis = 10.seconds.inWholeMilliseconds
                socketTimeoutMillis = 15.seconds.inWholeMilliseconds
            }

            // SMART RETRY (Ketahanan Skala Global)
            install(HttpRequestRetry) {
                maxRetries = 3
                retryIf { _, response ->
                    // Hanya retry jika server error (500-599)
                    response.status.value in 500..599
                }
                exponentialDelay()
            }
        }
    }
}
