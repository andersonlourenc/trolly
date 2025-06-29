package com.lourenc.trolly.domain.usecase.impl

import com.lourenc.trolly.data.local.entity.ItemLista
import com.lourenc.trolly.data.repository.ProdutoMercado
import com.lourenc.trolly.data.repository.interfaces.ItemListaRepository
import com.lourenc.trolly.domain.result.ItemListaResult
import com.lourenc.trolly.domain.strategy.*
import com.lourenc.trolly.domain.usecase.ItemListaUseCase

class ItemListaUseCaseImpl(
    private val itemRepository: ItemListaRepository
) : ItemListaUseCase {

    override suspend fun adicionarItem(item: ItemLista): ItemListaResult {
        return try {
            itemRepository.inserirItem(item)
            ItemListaResult.ItemSuccess(item)
        } catch (e: Exception) {
            ItemListaResult.Error("Erro ao adicionar item: ${e.message}", e)
        }
    }

    override suspend fun adicionarOuIncrementarItem(item: ItemLista): ItemListaResult {
        return try {
            val itemResultado = itemRepository.adicionarOuIncrementarItem(item)
            ItemListaResult.ItemSuccess(itemResultado)
        } catch (e: Exception) {
            ItemListaResult.Error("Erro ao adicionar ou incrementar item: ${e.message}", e)
        }
    }

    override suspend fun atualizarItem(item: ItemLista): ItemListaResult {
        return try {
            itemRepository.atualizarItem(item)
            ItemListaResult.ItemSuccess(item)
        } catch (e: Exception) {
            ItemListaResult.Error("Erro ao atualizar item: ${e.message}", e)
        }
    }

    override suspend fun removerItem(item: ItemLista): ItemListaResult {
        return try {
            itemRepository.deletarItem(item)
            ItemListaResult.Success("Item removido com sucesso")
        } catch (e: Exception) {
            ItemListaResult.Error("Erro ao remover item: ${e.message}", e)
        }
    }

    override suspend fun getItensPorLista(idLista: Int): ItemListaResult {
        return try {
            val itens = itemRepository.getItensPorLista(idLista)
            ItemListaResult.ItensSuccess(itens)
        } catch (e: Exception) {
            ItemListaResult.Error("Erro ao carregar itens: ${e.message}", e)
        }
    }

    override suspend fun getItensPorListaOrdenados(idLista: Int, strategy: ItemSortingStrategy): ItemListaResult {
        return try {
            val itens = itemRepository.getItensPorLista(idLista)
            val sorter = ItemSorter(strategy)
            val itensOrdenados = sorter.sortItems(itens)
            ItemListaResult.ItensSuccess(itensOrdenados)
        } catch (e: Exception) {
            ItemListaResult.Error("Erro ao carregar itens ordenados: ${e.message}", e)
        }
    }

    override suspend fun marcarItemComoComprado(itemId: Int, comprado: Boolean): ItemListaResult {
        return try {
            itemRepository.marcarItemComoComprado(itemId, comprado)
            ItemListaResult.Success("Item marcado como ${if (comprado) "comprado" else "não comprado"}")
        } catch (e: Exception) {
            ItemListaResult.Error("Erro ao marcar item: ${e.message}", e)
        }
    }

    override suspend fun calcularTotalLista(idLista: Int): ItemListaResult {
        return try {
            val total = itemRepository.calcularTotalLista(idLista)
            ItemListaResult.TotalListaSuccess(total)
        } catch (e: Exception) {
            ItemListaResult.Error("Erro ao calcular total: ${e.message}", e)
        }
    }

    override suspend fun getItensComprados(idLista: Int): ItemListaResult {
        return try {
            val itens = itemRepository.getItensComprados(idLista)
            ItemListaResult.ItensSuccess(itens)
        } catch (e: Exception) {
            ItemListaResult.Error("Erro ao carregar itens comprados: ${e.message}", e)
        }
    }

    override suspend fun getItensNaoComprados(idLista: Int): ItemListaResult {
        return try {
            val itens = itemRepository.getItensNaoComprados(idLista)
            ItemListaResult.ItensSuccess(itens)
        } catch (e: Exception) {
            ItemListaResult.Error("Erro ao carregar itens não comprados: ${e.message}", e)
        }
    }

    override fun pesquisarProdutos(termo: String): List<ProdutoMercado> {
        return itemRepository.filtrarProdutos(termo)
    }

    override fun converterProdutoParaItem(produto: ProdutoMercado, listaId: Int, quantidade: Int): ItemLista {
        return ItemLista(
            idLista = listaId,
            name = produto.nome,
            quantidade = quantidade,
            unidade = produto.unidade,
            precoUnitario = produto.preco,
            comprado = false
        )
    }

    override fun getEstrategiasOrdenacao(): List<ItemSortingStrategy> {
        return listOf(
            AlphabeticalSortStrategy(),
            PriceSortStrategy(),
            PriceDescendingSortStrategy(),
            QuantitySortStrategy(),
            TotalPriceSortStrategy(),
            StatusSortStrategy(),
            CategorySortStrategy()
        )
    }
} 