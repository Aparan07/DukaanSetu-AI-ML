package com.example.dukaansetu.ai

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProductEntity::class,
        UdhaarEntity::class,
        SaleEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DukaanDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    abstract fun udhaarDao(): UdhaarDao

    abstract fun saleDao(): SaleDao

    companion object {

        @Volatile
        private var INSTANCE: DukaanDatabase? = null

        fun getDatabase(context: Context): DukaanDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DukaanDatabase::class.java,
                    "dukaan_setu_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}