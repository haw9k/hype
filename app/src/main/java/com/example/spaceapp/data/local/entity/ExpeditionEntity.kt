package com.example.spaceapp.data.local.entity

import androidx.room.*

@Entity(
    tableName = "expeditions",
    foreignKeys = [
        ForeignKey(
            entity = PlanetEntity::class,
            parentColumns = ["id"],
            childColumns = ["planetId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("planetId")]
)
data class ExpeditionEntity(
    @PrimaryKey val id: Long,
    val planetId: Long,
    val missionName: String,
    val commanderName: String,
    val phone: String,
    val dateMillis: Long
)
