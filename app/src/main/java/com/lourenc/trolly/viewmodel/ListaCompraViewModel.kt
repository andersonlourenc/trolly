package com.lourenc.trolly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.lourenc.trolly.data.local.entity.ListaCompra
import com.lourenc.trolly.data.repository.ListaCompraRepository

class  ListaCompraViewModel(private val repository: ListaCompraRepository) : ViewModel() {

    val todasListas: LiveData<List<ListaCompra>> = repository.getAllListas().asLiveData()

    fun addLista(lista: ListaCompra) {
        viewModelScope.launch {
            repository.insertLista(lista)
        }
    }

    fun deleteLista(lista: ListaCompra) {
        viewModelScope.launch {
            repository.deleteLista(lista)
        }
    }

    fun updateLista(lista: ListaCompra) {
        viewModelScope.launch {
            repository.updateLista(lista)
        }
    }

    suspend fun getListaById(id: Int): ListaCompra? {
        return repository.getListaById(id)
    }
}
