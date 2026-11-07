package com.barcode.tes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.barcode.tes.data.local.dao.*
import com.barcode.tes.data.local.entity.*

@Database(
    entities = [
        ProductEntity::class,
        BannerEntity::class,
        OrderEntity::class,
        AddressEntity::class,
        ProfileEntity::class,
        CategoryEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun bannerDao(): BannerDao
    abstract fun orderDao(): OrderDao
    abstract fun addressDao(): AddressDao
    abstract fun profileDao(): ProfileDao
    abstract fun categoryDao(): CategoryDao
}
