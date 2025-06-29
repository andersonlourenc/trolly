package com.lourenc.trolly.domain.builder

import com.lourenc.trolly.data.local.entity.ItemLista
import com.lourenc.trolly.data.local.entity.ListaCompra
import com.lourenc.trolly.data.repository.ProdutoMercado
import com.lourenc.trolly.domain.model.ListaCompraType
import java.util.Calendar

class ListaCompraBuilder {
    private var nome: String = ""
    private var descricao: String = ""
    private var tipo: ListaCompraType = ListaCompraType.Regular
    private var itens: MutableList<ItemLista> = mutableListOf()
    private var dataCriacao: Long = System.currentTimeMillis()
    private var totalEstimado: Double = 0.0
    private var fotoCapa: String? = null
    private var idLista: Int = 0
    
    fun withNome(nome: String): ListaCompraBuilder {
        this.nome = nome
        return this
    }
    
    fun withDescricao(descricao: String): ListaCompraBuilder {
        this.descricao = descricao
        return this
    }
    
    fun withTipo(tipo: ListaCompraType): ListaCompraBuilder {
        this.tipo = tipo
        return this
    }
    
    fun withDataCriacao(dataCriacao: Long): ListaCompraBuilder {
        this.dataCriacao = dataCriacao
        return this
    }
    
    fun withFotoCapa(fotoCapa: String?): ListaCompraBuilder {
        this.fotoCapa = fotoCapa
        return this
    }
    
    fun withId(id: Int): ListaCompraBuilder {
        this.idLista = id
        return this
    }
    
    fun addItem(item: ItemLista): ListaCompraBuilder {
        itens.add(item)
        totalEstimado += item.precoUnitario * item.quantidade
        return this
    }
    
    fun addItem(nome: String, quantidade: Int, unidade: String, precoUnitario: Double, comprado: Boolean = false): ListaCompraBuilder {
        val item = ItemLista(
            idLista = idLista,
            name = nome,
            quantidade = quantidade,
            unidade = unidade,
            precoUnitario = precoUnitario,
            comprado = comprado
        )
        return addItem(item)
    }
    
    fun addProduto(produto: ProdutoMercado, quantidade: Int = 1, comprado: Boolean = false): ListaCompraBuilder {
        return addItem(
            nome = produto.nome,
            quantidade = quantidade,
            unidade = produto.unidade,
            precoUnitario = produto.preco,
            comprado = comprado
        )
    }
    
    fun addItens(items: List<ItemLista>): ListaCompraBuilder {
        items.forEach { addItem(it) }
        return this
    }
    
    fun addProdutos(produtos: List<ProdutoMercado>): ListaCompraBuilder {
        produtos.forEach { addProduto(it) }
        return this
    }
    
    fun clearItems(): ListaCompraBuilder {
        itens.clear()
        totalEstimado = 0.0
        return this
    }
    
    fun withDefaultNome(): ListaCompraBuilder {
        if (nome.isBlank()) {
            nome = when (tipo) {
                is ListaCompraType.Weekly -> {
                    val calendar = Calendar.getInstance()
                    val weekNumber = calendar.get(Calendar.WEEK_OF_YEAR)
                    val year = calendar.get(Calendar.YEAR)
                    "Lista Semanal ${weekNumber}/${year}"
                }
                is ListaCompraType.Monthly -> {
                    val calendar = Calendar.getInstance()
                    val month = calendar.get(Calendar.MONTH)
                    val year = calendar.get(Calendar.YEAR)
                    val monthName = getMonthName(month)
                    "Lista Mensal ${monthName}/${year}"
                }
                is ListaCompraType.Emergency -> "Lista de Emergência"
                is ListaCompraType.Recurrent -> "Lista Recorrente"
                else -> "Nova Lista"
            }
        }
        return this
    }
    
    fun withDefaultDescricao(): ListaCompraBuilder {
        if (descricao.isBlank()) {
            descricao = when (tipo) {
                is ListaCompraType.Weekly -> {
                    val calendar = Calendar.getInstance()
                    val weekNumber = calendar.get(Calendar.WEEK_OF_YEAR)
                    "Lista de compras da semana ${weekNumber}"
                }
                is ListaCompraType.Monthly -> {
                    val calendar = Calendar.getInstance()
                    val month = calendar.get(Calendar.MONTH)
                    val monthName = getMonthName(month)
                    "Lista de compras de ${monthName}"
                }
                is ListaCompraType.Emergency -> "Lista para compras urgentes"
                is ListaCompraType.Recurrent -> "Lista que se repete periodicamente"
                else -> "Lista de compras"
            }
        }
        return this
    }
    
    fun build(): ListaCompra {
        // Aplicar nomes padrão se necessário
        withDefaultNome()
        withDefaultDescricao()
        
        return ListaCompra(
            id = idLista,
            name = nome,
            descricao = descricao,
            dataCriacao = dataCriacao,
            totalEstimado = totalEstimado,
            fotoCapa = fotoCapa
        )
    }
    
    fun buildWithItems(): Pair<ListaCompra, List<ItemLista>> {
        val lista = build()
        return Pair(lista, itens.toList())
    }
    
    private fun getMonthName(month: Int): String {
        return when (month) {
            Calendar.JANUARY -> "Janeiro"
            Calendar.FEBRUARY -> "Fevereiro"
            Calendar.MARCH -> "Março"
            Calendar.APRIL -> "Abril"
            Calendar.MAY -> "Maio"
            Calendar.JUNE -> "Junho"
            Calendar.JULY -> "Julho"
            Calendar.AUGUST -> "Agosto"
            Calendar.SEPTEMBER -> "Setembro"
            Calendar.OCTOBER -> "Outubro"
            Calendar.NOVEMBER -> "Novembro"
            Calendar.DECEMBER -> "Dezembro"
            else -> "Desconhecido"
        }
    }
    
    companion object {
        fun createRegularLista(nome: String): ListaCompraBuilder {
            return ListaCompraBuilder()
                .withTipo(ListaCompraType.Regular)
                .withNome(nome)
        }
        
        fun createWeeklyLista(): ListaCompraBuilder {
            return ListaCompraBuilder()
                .withTipo(ListaCompraType.Weekly)
        }
        
        fun createMonthlyLista(): ListaCompraBuilder {
            return ListaCompraBuilder()
                .withTipo(ListaCompraType.Monthly)
        }
        
        fun createEmergencyLista(): ListaCompraBuilder {
            return ListaCompraBuilder()
                .withTipo(ListaCompraType.Emergency)
                .withNome("Lista de Emergência")
        }
    }
} 