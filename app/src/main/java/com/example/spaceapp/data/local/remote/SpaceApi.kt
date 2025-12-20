package com.example.spaceapp.data.remote

import com.example.spaceapp.data.remote.dto.*
import retrofit2.http.*

interface SpaceApi {
    @GET("galaxies")
    suspend fun getGalaxies(): List<GalaxyDto>

    @GET("planets")
    suspend fun getPlanets(@Query("galaxyId") galaxyId: Long? = null): List<PlanetDto>

    @GET("expeditions")
    suspend fun getExpeditions(@Query("planetId") planetId: Long? = null): List<ExpeditionDto>

    @POST("expeditions")
    suspend fun createExpedition(@Body dto: ExpeditionDto): ExpeditionDto

    @PUT("expeditions/{id}")
    suspend fun updateExpedition(@Path("id") id: Long, @Body dto: ExpeditionDto): ExpeditionDto

    @DELETE("expeditions/{id}")
    suspend fun deleteExpedition(@Path("id") id: Long)
}
