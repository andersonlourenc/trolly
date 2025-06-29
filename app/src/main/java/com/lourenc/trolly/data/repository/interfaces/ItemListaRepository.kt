package com.lourenc.trolly.data.repository.interfaces

import com.lourenc.trolly.data.local.entity.ItemLista
import com.lourenc.trolly.data.repository.ProdutoMercado

interface ItemListaRepository {
    suspend fun inserirItem(item: ItemLista)
    suspend fun atualizarItem(item: ItemLista)
    suspend fun deletarItem(item: ItemLista)
    suspend fun getItensPorLista(idLista: Int): List<ItemLista>
    suspend fun getItensComprados(idLista: Int): List<ItemLista>
    suspend fun getItensNaoComprados(idLista: Int): List<ItemLista>
    suspend fun marcarItemComoComprado(itemId: Int, comprado: Boolean)
    suspend fun calcularTotalLista(idLista: Int): Double
    fun getProdutosPredefinidos(): List<ProdutoMercado>
    fun filtrarProdutos(termo: String): List<ProdutoMercado>
} 