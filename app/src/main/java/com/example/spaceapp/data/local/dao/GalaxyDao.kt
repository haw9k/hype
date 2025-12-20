package com.example.spaceapp.data.local.dao

import androidx.room.*
import com.example.spaceapp.data.local.entity.GalaxyEntity

@Dao
interface GalaxyDao {
    @Query("SELECT * FROM galaxies ORDER BY name")
    suspend fun getAll(): List<GalaxyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: GalaxyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<GalaxyEntity>)

    @Delete
    suspend fun delete(item: GalaxyEntity)
}
