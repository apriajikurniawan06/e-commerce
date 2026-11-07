package com.barcode.tes

import android.app.Application
import androidx.room.Room
import com.barcode.tes.data.local.AppDatabase
import net.sqlcipher.database.SupportFactory
import net.sqlcipher.database.SQLiteDatabase

class TesApplication : Application() {
    
    companion object {
        private var _database: AppDatabase? = null
        val database: AppDatabase
            get() = _database ?: synchronized(this) {
                _database ?: throw IllegalStateException("Database belum diinisialisasi")
            }
    }

    override fun onCreate() {
        super.onCreate()
        
        // PENTING: Inisialisasi library SQLCipher
        SQLiteDatabase.loadLibs(this)
        
        // Enkripsi database lokal menggunakan SQLCipher
        val dbPassword = BuildConfig.DB_PASSWORD
        // Cara yang lebih aman dan direkomendasikan untuk SQLCipher
        val passphrase = SQLiteDatabase.getBytes(dbPassword.toCharArray())
        
        val factory = SupportFactory(passphrase)

        _database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "tes_database_v7" // Naikkan lagi ke v7 untuk memastikan file benar-benar baru
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }
}
