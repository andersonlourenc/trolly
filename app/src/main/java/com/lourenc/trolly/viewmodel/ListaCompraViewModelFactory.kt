package com.lourenc.trolly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lourenc.trolly.data.repository.ItemListaRepository
import com.lourenc.trolly.data.repository.ListaCompraRepository

class ListaCompraViewModelFactory(
    private val repository: ListaCompraRepository,
    private val itemRepository: ItemListaRepository? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListaCompraViewModel::class.java)) {
            return ListaCompraViewModel(repository, itemRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
