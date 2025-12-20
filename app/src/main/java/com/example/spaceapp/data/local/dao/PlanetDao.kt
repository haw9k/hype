package com.example.spaceapp.data.local.dao

import androidx.room.*
import com.example.spaceapp.data.local.entity.PlanetEntity

@Dao
interface PlanetDao {
    @Query("SELECT * FROM planets WHERE galaxyId = :galaxyId ORDER BY name")
    suspend fun getByGalaxy(galaxyId: Long): List<PlanetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: PlanetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<PlanetEntity>)

    @Delete
    suspend fun delete(item: PlanetEntity)
}
