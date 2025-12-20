package com.example.spaceapp.data.repository

import com.example.spaceapp.data.local.AppDatabase
import com.example.spaceapp.data.local.entity.*
import com.example.spaceapp.data.remote.RetrofitProvider
import com.example.spaceapp.data.remote.dto.*

class SpaceRepository(private val db: AppDatabase) {

    private val api = RetrofitProvider.api

    // --------- Local reads ---------
    suspend fun getGalaxiesLocal() = db.galaxyDao().getAll()
    suspend fun getPlanetsLocal(galaxyId: Long) = db.planetDao().getByGalaxy(galaxyId)
    suspend fun getExpeditionsLocal(planetId: Long) = db.expeditionDao().getByPlanet(planetId)

    // --------- Local writes ---------
    suspend fun upsertGalaxyLocal(e: GalaxyEntity) = db.galaxyDao().upsert(e)
    suspend fun upsertPlanetLocal(e: PlanetEntity) = db.planetDao().upsert(e)
    suspend fun upsertExpeditionLocal(e: ExpeditionEntity) = db.expeditionDao().upsert(e)

    suspend fun deleteGalaxyLocal(e: GalaxyEntity) = db.galaxyDao().delete(e)
    suspend fun deletePlanetLocal(e: PlanetEntity) = db.planetDao().delete(e)
    suspend fun deleteExpeditionLocal(e: ExpeditionEntity) = db.expeditionDao().delete(e)

    // --------- Sync (Retrofit -> Room) ---------
    suspend fun syncGalaxies() {
        val remote = api.getGalaxies()
        db.galaxyDao().upsertAll(remote.map { it.toEntity() })
    }

    suspend fun syncPlanets(galaxyId: Long) {
        val remote = api.getPlanets(galaxyId)
        db.planetDao().upsertAll(remote.map { it.toEntity() })
    }

    suspend fun syncExpeditions(planetId: Long) {
        val remote = api.getExpeditions(planetId)
        db.expeditionDao().upsertAll(remote.map { it.toEntity() })
    }

    // --------- Remote write for expeditions ---------
    suspend fun createOrUpdateExpeditionRemote(entity: ExpeditionEntity) {
        val dto = entity.toDto()
        val saved = runCatching {
            // Простейшая логика: если на сервере нет, создадим.
            api.updateExpedition(dto.id, dto)
        }.getOrElse {
            api.createExpedition(dto)
        }
        db.expeditionDao().upsert(saved.toEntity())
    }

    suspend fun deleteExpeditionRemote(entity: ExpeditionEntity) {
        runCatching { api.deleteExpedition(entity.id) }
        db.expeditionDao().delete(entity)
    }

    // --------- Mappers ---------
    private fun GalaxyDto.toEntity() = GalaxyEntity(id, name, type)
    private fun PlanetDto.toEntity() = PlanetEntity(id, galaxyId, name, planetClass, radiusKm)
    private fun ExpeditionDto.toEntity() =
        ExpeditionEntity(id, planetId, missionName, commanderName, phone, dateMillis)

    private fun ExpeditionEntity.toDto() =
        ExpeditionDto(id, planetId, missionName, commanderName, phone, dateMillis)
}
