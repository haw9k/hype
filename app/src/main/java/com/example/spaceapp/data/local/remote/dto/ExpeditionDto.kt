package com.example.spaceapp.data.remote.dto

data class ExpeditionDto(
    val id: Long,
    val planetId: Long,
    val missionName: String,
    val commanderName: String,
    val phone: String,
    val dateMillis: Long
)

