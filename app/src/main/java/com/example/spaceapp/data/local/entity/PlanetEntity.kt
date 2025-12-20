package com.example.spaceapp.data.local.entity

import androidx.room.*

@Entity(
    tableName = "planets",
    foreignKeys = [
        ForeignKey(
            entity = GalaxyEntity::class,
            parentColumns = ["id"],
            childColumns = ["galaxyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("galaxyId")]
)
data class PlanetEntity(
    @PrimaryKey val id: Long,
    val galaxyId: Long,
    val name: String,
    val planetClass: String,
    val radiusKm: Double
)
