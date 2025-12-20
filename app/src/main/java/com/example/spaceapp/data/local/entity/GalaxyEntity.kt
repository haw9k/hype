package com.example.spaceapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "galaxies")
data class GalaxyEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val type: String
)
