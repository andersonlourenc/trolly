package com.lourenc.trolly.data.repository.impl

import com.lourenc.trolly.data.local.dao.ItemListaDao
import com.lourenc.trolly.data.local.entity.ItemLista
import com.lourenc.trolly.data.repository.ProdutoMercado
import com.lourenc.trolly.data.repository.interfaces.ItemListaRepository

class ItemListaRepositoryImpl(private val itemDao: ItemListaDao) : ItemListaRepository {
    
    override suspend fun inserirItem(item: ItemLista) {
        itemDao.inserir(item)
    }
    
    override suspend fun atualizarItem(item: ItemLista) {
        itemDao.atualizar(item)
    }
    
    override suspend fun deletarItem(item: ItemLista) {
        itemDao.deletar(item)
    }
    
    override suspend fun getItensPorLista(idLista: Int): List<ItemLista> {
        return itemDao.getItensPorLista(idLista)
    }
    
    override suspend fun getItensComprados(idLista: Int): List<ItemLista> {
        return itemDao.getItensPorLista(idLista).filter { it.comprado }
    }
    
    override suspend fun getItensNaoComprados(idLista: Int): List<ItemLista> {
        return itemDao.getItensPorLista(idLista).filter { !it.comprado }
    }
    
    override suspend fun marcarItemComoComprado(itemId: Int, comprado: Boolean) {
        // Implementar quando o DAO tiver esse método
        // itemDao.marcarComoComprado(itemId, comprado)
    }
    
    override suspend fun calcularTotalLista(idLista: Int): Double {
        val itens = getItensPorLista(idLista)
        return itens.sumOf { it.precoUnitario * it.quantidade }
    }
    
    override fun getProdutosPredefinidos(): List<ProdutoMercado> {
        return listOf(
            ProdutoMercado("Arroz Integral", "kg", 6.99),
            ProdutoMercado("Feijão Carioca", "kg", 7.50),
            ProdutoMercado("Leite Integral", "L", 4.99),
            ProdutoMercado("Café em Pó", "pacote", 15.90),
            ProdutoMercado("Açúcar", "kg", 3.99),
            ProdutoMercado("Sal Refinado", "kg", 2.50),
            ProdutoMercado("Óleo de Soja", "garrafa", 9.80),
            ProdutoMercado("Macarrão", "pacote", 3.99),
            ProdutoMercado("Farinha de Trigo", "kg", 5.20),
            ProdutoMercado("Ovos", "dúzia", 12.99),
            ProdutoMercado("Pão Francês", "unidade", 0.75),
            ProdutoMercado("Queijo Mussarela", "kg", 39.90),
            ProdutoMercado("Presunto", "kg", 32.50),
            ProdutoMercado("Margarina", "pote", 7.99),
            ProdutoMercado("Iogurte", "unidade", 3.49),
            ProdutoMercado("Banana", "kg", 5.99),
            ProdutoMercado("Maçã", "kg", 9.99),
            ProdutoMercado("Laranja", "kg", 4.50),
            ProdutoMercado("Batata", "kg", 3.99),
            ProdutoMercado("Cenoura", "kg", 4.50),
            ProdutoMercado("Cebola", "kg", 5.99),
            ProdutoMercado("Tomate", "kg", 7.99),
            ProdutoMercado("Alface", "unidade", 2.99),
            ProdutoMercado("Frango", "kg", 16.99),
            ProdutoMercado("Carne Moída", "kg", 29.90),
            ProdutoMercado("Sabão em Pó", "caixa", 12.99),
            ProdutoMercado("Detergente", "unidade", 2.50),
            ProdutoMercado("Papel Higiênico", "pacote", 19.90),
            ProdutoMercado("Sabonete", "unidade", 2.99),
            ProdutoMercado("Shampoo", "unidade", 13.99)
        )
    }
    
    override fun filtrarProdutos(termo: String): List<ProdutoMercado> {
        if (termo.isBlank()) return getProdutosPredefinidos().take(5)
        
        return getProdutosPredefinidos().filter { 
            it.nome.contains(termo, ignoreCase = true)
        }
    }
} 