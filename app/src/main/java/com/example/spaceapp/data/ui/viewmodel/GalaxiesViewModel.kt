package com.example.spaceapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.spaceapp.data.local.AppDatabase
import com.example.spaceapp.data.local.entity.GalaxyEntity
import com.example.spaceapp.data.repository.SpaceRepository
import kotlinx.coroutines.launch

class GalaxiesViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = SpaceRepository(AppDatabase.get(app))

    private val _items = MutableLiveData<List<GalaxyEntity>>(emptyList())
    val items: LiveData<List<GalaxyEntity>> = _items

    fun loadLocal() = viewModelScope.launch {
        _items.value = repo.getGalaxiesLocal()
    }

    fun sync() = viewModelScope.launch {
        repo.syncGalaxies()
        loadLocal()
    }

    fun upsert(entity: GalaxyEntity) = viewModelScope.launch {
        repo.upsertGalaxyLocal(entity)
        loadLocal()
    }

    fun delete(entity: GalaxyEntity) = viewModelScope.launch {
        repo.deleteGalaxyLocal(entity)
        loadLocal()
    }
}
