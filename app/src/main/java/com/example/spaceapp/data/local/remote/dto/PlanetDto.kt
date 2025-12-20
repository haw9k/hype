package com.example.spaceapp.data.remote.dto

data class PlanetDto(
    val id: Long,
    val galaxyId: Long,
    val name: String,
    val planetClass: String,
    val radiusKm: Double
)
