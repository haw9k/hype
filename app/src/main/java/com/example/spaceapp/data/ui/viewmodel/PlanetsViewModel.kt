package com.example.spaceapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.spaceapp.data.local.AppDatabase
import com.example.spaceapp.data.local.entity.PlanetEntity
import com.example.spaceapp.data.repository.SpaceRepository
import kotlinx.coroutines.launch

class PlanetsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = SpaceRepository(AppDatabase.get(app))

    private val _items = MutableLiveData<List<PlanetEntity>>(emptyList())
    val items: LiveData<List<PlanetEntity>> = _items

    fun loadLocal(galaxyId: Long) = viewModelScope.launch {
        _items.value = repo.getPlanetsLocal(galaxyId)
    }

    fun sync(galaxyId: Long) = viewModelScope.launch {
        repo.syncPlanets(galaxyId)
        loadLocal(galaxyId)
    }

    fun upsert(entity: PlanetEntity, galaxyId: Long) = viewModelScope.launch {
        repo.upsertPlanetLocal(entity)
        loadLocal(galaxyId)
    }

    fun delete(entity: PlanetEntity, galaxyId: Long) = viewModelScope.launch {
        repo.deletePlanetLocal(entity)
        loadLocal(galaxyId)
    }
}
