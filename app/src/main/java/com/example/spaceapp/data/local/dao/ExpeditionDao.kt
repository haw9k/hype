package com.example.spaceapp.data.local.dao

import androidx.room.*
import com.example.spaceapp.data.local.entity.ExpeditionEntity

@Dao
interface ExpeditionDao {
    @Query("SELECT * FROM expeditions WHERE planetId = :planetId ORDER BY dateMillis DESC")
    suspend fun getByPlanet(planetId: Long): List<ExpeditionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ExpeditionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ExpeditionEntity>)

    @Delete
    suspend fun delete(item: ExpeditionEntity)
}
