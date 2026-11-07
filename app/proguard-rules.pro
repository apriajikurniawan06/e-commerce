# Keamanan tingkat tinggi: Mengacak kode agar sulit dibaca (Obfuscation)
-keepattributes *Annotation*, Signature, InnerClasses

# Proteksi Model Data agar tetap bisa dibaca oleh Serializer tapi nama aslinya disamarkan
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}

# Supabase & Ktor Security
-keep class io.github.jan.supabase.** { *; }
-keep class io.ktor.** { *; }
-keep class net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**

# Menghapus Log secara otomatis di versi Release (Mencegah kebocoran info lewat Logcat)
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** e(...);
}

# Optimasi tambahan
-optimizationpasses 5
-allowaccessmodification
