package com.example.spaceapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.spaceapp.data.local.AppDatabase
import com.example.spaceapp.data.local.entity.ExpeditionEntity
import com.example.spaceapp.data.repository.SpaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpeditionsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = SpaceRepository(AppDatabase.get(app))

    private val _items = MutableLiveData<List<ExpeditionEntity>>(emptyList())
    val items: LiveData<List<ExpeditionEntity>> = _items

    val editing = MutableLiveData<ExpeditionEntity?>(null)

    // Toast-сообщения
    private val _toast = MutableLiveData<String>()
    val toast: LiveData<String> = _toast

    fun loadLocal(planetId: Long) = viewModelScope.launch(Dispatchers.IO) {
        _items.postValue(repo.getExpeditionsLocal(planetId))
    }


    fun sync(planetId: Long) = viewModelScope.launch(Dispatchers.IO) {
        val ok = runCatching { repo.syncExpeditions(planetId) }.isSuccess
        _toast.postValue(if (ok) "Синхронизация выполнена" else "Сервер недоступен")
        loadLocal(planetId)
    }


    fun save(entity: ExpeditionEntity) = viewModelScope.launch(Dispatchers.IO) {
        repo.upsertExpeditionLocal(entity)
        loadLocal(entity.planetId)
        editing.postValue(null)
    }


    fun delete(entity: ExpeditionEntity) = viewModelScope.launch(Dispatchers.IO) {
        repo.deleteExpeditionLocal(entity)
        loadLocal(entity.planetId)
        if (editing.value?.id == entity.id) editing.postValue(null)
    }
}
