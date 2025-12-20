package com.example.spaceapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.spaceapp.data.local.dao.ExpeditionDao
import com.example.spaceapp.data.local.dao.GalaxyDao
import com.example.spaceapp.data.local.dao.PlanetDao
import com.example.spaceapp.data.local.entity.ExpeditionEntity
import com.example.spaceapp.data.local.entity.GalaxyEntity
import com.example.spaceapp.data.local.entity.PlanetEntity

@Database(
    entities = [GalaxyEntity::class, PlanetEntity::class, ExpeditionEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun galaxyDao(): GalaxyDao
    abstract fun planetDao(): PlanetDao
    abstract fun expeditionDao(): ExpeditionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "space.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }

}
