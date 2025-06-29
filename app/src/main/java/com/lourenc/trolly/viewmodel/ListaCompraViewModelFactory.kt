package com.lourenc.trolly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lourenc.trolly.domain.usecase.ItemListaUseCase
import com.lourenc.trolly.domain.usecase.ListaCompraUseCase

class ListaCompraViewModelFactory(
    private val listaUseCase: ListaCompraUseCase,
    private val itemUseCase: ItemListaUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListaCompraViewModel::class.java)) {
            return ListaCompraViewModel(listaUseCase, itemUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
