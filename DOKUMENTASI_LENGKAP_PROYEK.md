# DOKUMENTASI LENGKAP PEMBUATAN APLIKASI (TES APP)

Dokumen ini berisi panduan teknis, struktur proyek, dan source code lengkap untuk membangun aplikasi Android berbasis Clean Architecture dengan integrasi Supabase dan PPOB.

---

## 1. PANDUAN PEMBUATAN DARI NOL (STEP-BY-STEP)

### A. TAHAP SETUP DASAR (CORE)
1. **Supabase Client**: Inisialisasi koneksi ke Cloud di `data/remote/SupabaseClient.kt`. Gunakan plugin Auth, Postgrest (Database), dan Storage.
2. **Room Database**: Setup database lokal di `data/local/`. Buat Entity (tabel), DAO (query), dan AppDatabase. Inisialisasi di `TesApplication.kt`.

### B. MEMBANGUN DOMAIN LAYER (KONTRAK)
1. **Model**: Buat data class murni Kotlin di `domain/model/`. Gunakan `@Serializable` agar bisa diproses sebagai JSON.
2. **Repository Interface**: Tentukan fungsi apa saja yang tersedia (misal: `getProducts`, `login`).

### C. MEMBANGUN DATA LAYER (LOGIKA)
1. **Mappers**: Buat fungsi ekstensi untuk mengubah `Entity` (Lokal) menjadi `Domain Model` (Murni) dan sebaliknya.
2. **Implementation**: Buat logika di `data/repository/`. Gunakan pola **Offline-First**:
   - Ambil data dari internet (Supabase/API).
   - Jika berhasil, simpan ke Room sebagai cache.
   - Jika gagal, ambil data terakhir dari Room.

### D. DEPENDENCY INJECTION (DI)
1. Gunakan `AppModule.kt` sebagai pusat penyedia objek (HttpClient, Repository, DAO).
2. Tujuannya agar objek hanya dibuat satu kali (Singleton) dan hemat memori.

### E. UI & VIEWMODEL
1. **ViewModel**: Memegang status (state) UI dan memanggil Repository.
2. **Compose**: Menampilkan data menggunakan Jetpack Compose.

---

## 2. SOURCE CODE: KONFIGURASI (GRADLE)

### build.gradle.kts (Module: app)
```kotlin
// Dependency penting untuk Supabase, Ktor (API), Room, dan Compose
dependencies {
    implementation(libs.androidx.navigation.compose)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.gotrue)
    implementation(libs.supabase.storage)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
```

---

## 3. SOURCE CODE: INFRASTRUCTURE (CORE)

### SupabaseClient.kt
```kotlin
object SupabaseClient {
    const val URL = "YOUR_SUPABASE_URL"
    const val ANON_KEY = "YOUR_ANON_KEY"

    val client = createSupabaseClient(URL, ANON_KEY) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }
}
```

### TesApplication.kt
```kotlin
class TesApplication : Application() {
    companion object {
        lateinit var database: AppDatabase
    }
    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, AppDatabase::class.java, "tes_db").build()
    }
}
```

---

## 4. SOURCE CODE: DOMAIN LAYER

### Ppob.kt (Model)
```kotlin
@Serializable
data class PpobProduct(
    val code: String,
    val name: String,
    val price: Double,
    val category: String
)
```

### PpobRepository.kt (Interface)
```kotlin
interface PpobRepository {
    suspend fun getProducts(category: String): Result<List<PpobProduct>>
    suspend fun executeTransaction(code: String, target: String): Result<PpobTransactionResponse>
}
```

---

## 5. SOURCE CODE: DATA LAYER (IMPLEMENTATION)

### PpobRepositoryImpl.kt
```kotlin
class PpobRepositoryImpl(private val client: HttpClient) : PpobRepository {
    override suspend fun getProducts(category: String): Result<List<PpobProduct>> {
        return try {
            // Logika API via Ktor
            val response: List<PpobProduct> = client.get("URL_API").body()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

## 6. SOURCE CODE: DEPENDENCY INJECTION

### AppModule.kt
```kotlin
object AppModule {
    private val database get() = TesApplication.database
    
    private val client = HttpClient(Android) {
        install(ContentNegotiation) { json() }
    }

    val ppobRepository: PpobRepository by lazy { PpobRepositoryImpl(client) }
    val authRepository: AuthRepository by lazy { AuthRepositoryImpl(database.profileDao()) }
}
```

---

## 7. CARA EXPORT KE PDF
1. Buka file **DOKUMENTASI_LENGKAP_PROYEK.md** ini.
2. Jika menggunakan Android Studio, klik kanan pada konten file -> **Open in Browser**.
3. Di Browser, tekan **CTRL + P** (Print).
4. Pilih **Destination: Save as PDF**.
5. Simpan file PDF Anda.

---
*Dokumentasi ini dibuat secara otomatis untuk membantu pengembang memahami alur kerja aplikasi Tes.*
