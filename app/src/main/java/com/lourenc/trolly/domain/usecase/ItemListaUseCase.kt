package com.lourenc.trolly.domain.usecase

import com.lourenc.trolly.data.local.entity.ItemLista
import com.lourenc.trolly.data.repository.ProdutoMercado
import com.lourenc.trolly.domain.result.ItemListaResult
import com.lourenc.trolly.domain.strategy.ItemSortingStrategy

interface ItemListaUseCase {
    suspend fun adicionarItem(item: ItemLista): ItemListaResult
    suspend fun adicionarOuIncrementarItem(item: ItemLista): ItemListaResult
    suspend fun atualizarItem(item: ItemLista): ItemListaResult
    suspend fun removerItem(item: ItemLista): ItemListaResult
    suspend fun getItensPorLista(idLista: Int): ItemListaResult
    suspend fun getItensPorListaOrdenados(idLista: Int, strategy: ItemSortingStrategy): ItemListaResult
    suspend fun marcarItemComoComprado(itemId: Int, comprado: Boolean): ItemListaResult
    suspend fun calcularTotalLista(idLista: Int): ItemListaResult
    suspend fun getItensComprados(idLista: Int): ItemListaResult
    suspend fun getItensNaoComprados(idLista: Int): ItemListaResult
    fun pesquisarProdutos(termo: String): List<ProdutoMercado>
    fun converterProdutoParaItem(produto: ProdutoMercado, listaId: Int, quantidade: Int = 1): ItemLista
    fun getEstrategiasOrdenacao(): List<ItemSortingStrategy>
} 