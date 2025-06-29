package com.lourenc.trolly.domain.result

import com.lourenc.trolly.data.local.entity.ItemLista

sealed class ItemListaResult {
    data class Success(val data: Any) : ItemListaResult()
    data class Error(val message: String, val exception: Exception? = null) : ItemListaResult()
    object Loading : ItemListaResult()
    
    // Resultados específicos
    data class ItemSuccess(val item: ItemLista) : ItemListaResult()
    data class ItensSuccess(val itens: List<ItemLista>) : ItemListaResult()
    data class TotalListaSuccess(val total: Double) : ItemListaResult()
} 