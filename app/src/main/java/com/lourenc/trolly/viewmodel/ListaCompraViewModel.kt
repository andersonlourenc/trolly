package com.lourenc.trolly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.lourenc.trolly.data.local.entity.ItemLista
import com.lourenc.trolly.data.local.entity.ListaCompra
import com.lourenc.trolly.data.repository.ProdutoMercado
import com.lourenc.trolly.domain.builder.ListaCompraBuilder
import com.lourenc.trolly.domain.model.ListaCompraType
import com.lourenc.trolly.domain.result.ItemListaResult
import com.lourenc.trolly.domain.result.ListaCompraResult
import com.lourenc.trolly.domain.strategy.ItemSortingStrategy
import com.lourenc.trolly.domain.usecase.ItemListaUseCase
import com.lourenc.trolly.domain.usecase.ListaCompraUseCase
import com.lourenc.trolly.utils.ListaCompraFormatter
import java.util.Calendar

class ListaCompraViewModel(
    private val listaUseCase: ListaCompraUseCase,
    private val itemUseCase: ItemListaUseCase
) : ViewModel() {

    // LiveData para listas
    private val _listas = MutableLiveData<List<ListaCompra>>(emptyList())
    val listas: LiveData<List<ListaCompra>> = _listas
    
    // LiveData para listas ativas
    private val _listasAtivas = MutableLiveData<List<ListaCompra>>(emptyList())
    val listasAtivas: LiveData<List<ListaCompra>> = _listasAtivas
    
    // LiveData para listas concluídas
    private val _listasConcluidas = MutableLiveData<List<ListaCompra>>(emptyList())
    val listasConcluidas: LiveData<List<ListaCompra>> = _listasConcluidas
    
    // LiveData para os itens da lista atual
    private val _itensLista = MutableLiveData<List<ItemLista>>(emptyList())
    val itensLista: LiveData<List<ItemLista>> = _itensLista
    
    // LiveData para produtos filtrados na pesquisa
    private val _produtosFiltrados = MutableLiveData<List<ProdutoMercado>>(emptyList())
    val produtosFiltrados: LiveData<List<ProdutoMercado>> = _produtosFiltrados
    
    // LiveData para o gasto mensal
    private val _gastoMensal = MutableLiveData<Double>(0.0)
    val gastoMensal: LiveData<Double> = _gastoMensal
    
    // LiveData para o valor da última lista
    private val _valorUltimaLista = MutableLiveData<Double>(0.0)
    val valorUltimaLista: LiveData<Double> = _valorUltimaLista
    
    // LiveData para estados de loading e erro
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage
    
    // LiveData para estratégias de ordenação
    private val _estrategiasOrdenacao = MutableLiveData<List<ItemSortingStrategy>>(emptyList())
    val estrategiasOrdenacao: LiveData<List<ItemSortingStrategy>> = _estrategiasOrdenacao
    
    // LiveData para tipos de lista disponíveis
    private val _tiposLista = MutableLiveData<List<ListaCompraType>>(emptyList())
    val tiposLista: LiveData<List<ListaCompraType>> = _tiposLista

    init {
        carregarEstrategiasOrdenacao()
        carregarTiposLista()
    }

    // Métodos para gerenciar listas
    fun addLista(lista: ListaCompra) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                println("DEBUG: Iniciando criação da lista: ${lista.name}")
                when (val result = listaUseCase.createLista(lista)) {
                    is ListaCompraResult.ListaSuccess -> {
                        println("DEBUG: Lista criada com sucesso: ${result.lista.id}")
                        carregarListasAtivas()
                        carregarListasConcluidas()
                    }
                    is ListaCompraResult.Error -> {
                        println("DEBUG: Erro ao criar lista: ${result.message}")
                        _errorMessage.value = result.message
                    }
                    else -> {
                        println("DEBUG: Resultado inesperado ao criar lista")
                        _errorMessage.value = "Erro desconhecido ao criar lista"
                    }
                }
            } catch (e: Exception) {
                println("DEBUG: Exceção ao criar lista: ${e.message}")
                _errorMessage.value = "Erro ao criar lista: ${e.message}"
            }
            _isLoading.value = false
        }
    }
    
    // Método usando Factory Pattern
    fun createListaWithType(type: ListaCompraType, nome: String, descricao: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = listaUseCase.createListaWithType(type, nome, descricao)) {
                is ListaCompraResult.ListaSuccess -> {
                    carregarListas()
                }
                is ListaCompraResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao criar lista com tipo"
                }
            }
            _isLoading.value = false
        }
    }
    
    // Método usando Builder Pattern
    fun createListaWithBuilder(builder: ListaCompraBuilder) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val (lista, itens) = builder.buildWithItems()
                
                when (val result = listaUseCase.createLista(lista)) {
                    is ListaCompraResult.ListaSuccess -> {
                        // Adicionar itens se houver
                        if (itens.isNotEmpty()) {
                            for (item in itens) {
                                itemUseCase.adicionarItem(item.copy(idLista = result.lista.id))
                            }
                        }
                        carregarListas()
                    }
                    is ListaCompraResult.Error -> {
                        _errorMessage.value = result.message
                    }
                    else -> {
                        _errorMessage.value = "Erro desconhecido ao criar lista com builder"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao criar lista com builder: ${e.message}"
            }
            
            _isLoading.value = false
        }
    }

    fun deleteLista(lista: ListaCompra) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = listaUseCase.deleteLista(lista)) {
                is ListaCompraResult.Success -> {
                    carregarListas()
                }
                is ListaCompraResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao deletar lista"
                }
            }
            _isLoading.value = false
        }
    }

    fun updateLista(lista: ListaCompra) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = listaUseCase.updateLista(lista)) {
                is ListaCompraResult.ListaSuccess -> {
                    carregarListas()
                }
                is ListaCompraResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao atualizar lista"
                }
            }
            _isLoading.value = false
        }
    }

    suspend fun getListaById(id: Int): ListaCompra? {
        return when (val result = listaUseCase.getListaById(id)) {
            is ListaCompraResult.ListaSuccess -> result.lista
            is ListaCompraResult.Error -> {
                _errorMessage.value = result.message
                null
            }
            else -> null
        }
    }
    
    // Método para buscar listas por tipo
    fun getListasByType(type: ListaCompraType) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = listaUseCase.getListasByType(type)) {
                is ListaCompraResult.ListasSuccess -> {
                    _listas.value = result.listas
                }
                is ListaCompraResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao buscar listas por tipo"
                }
            }
            _isLoading.value = false
        }
    }
    
    // Funções para gerenciar itens da lista
    fun carregarItensLista(listaId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = itemUseCase.getItensPorLista(listaId)) {
                is ItemListaResult.ItensSuccess -> {
                    _itensLista.value = result.itens
                }
                is ItemListaResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao carregar itens"
                }
            }
            _isLoading.value = false
        }
    }
    
    // Método usando Strategy Pattern para ordenação
    fun carregarItensListaOrdenados(listaId: Int, strategy: ItemSortingStrategy) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = itemUseCase.getItensPorListaOrdenados(listaId, strategy)) {
                is ItemListaResult.ItensSuccess -> {
                    _itensLista.value = result.itens
                }
                is ItemListaResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao carregar itens ordenados"
                }
            }
            _isLoading.value = false
        }
    }
    
    fun adicionarItemLista(item: ItemLista) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = itemUseCase.adicionarItem(item)) {
                is ItemListaResult.ItemSuccess -> {
                    carregarItensLista(item.idLista)
                }
                is ItemListaResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao adicionar item"
                }
            }
            _isLoading.value = false
        }
    }
    
    fun atualizarItemLista(item: ItemLista) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = itemUseCase.atualizarItem(item)) {
                is ItemListaResult.ItemSuccess -> {
                    carregarItensLista(item.idLista)
                }
                is ItemListaResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao atualizar item"
                }
            }
            _isLoading.value = false
        }
    }
    
    fun removerItemLista(item: ItemLista) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = itemUseCase.removerItem(item)) {
                is ItemListaResult.Success -> {
                    carregarItensLista(item.idLista)
                }
                is ItemListaResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao remover item"
                }
            }
            _isLoading.value = false
        }
    }
    
    // Pesquisar produtos
    fun pesquisarProdutos(termo: String) {
        val produtos = itemUseCase.pesquisarProdutos(termo)
        _produtosFiltrados.value = produtos
    }
    
    // Converter produto para item de lista
    fun converterProdutoParaItem(produto: ProdutoMercado, listaId: Int, quantidade: Int = 1): ItemLista {
        return itemUseCase.converterProdutoParaItem(produto, listaId, quantidade)
    }
    
    // Calcular o gasto mensal
    fun calcularGastoMensal() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val calendar = Calendar.getInstance()
            val mesAtual = calendar.get(Calendar.MONTH)
            val anoAtual = calendar.get(Calendar.YEAR)
            
            when (val result = listaUseCase.calcularGastoMensal(mesAtual, anoAtual)) {
                is ListaCompraResult.GastoMensalSuccess -> {
                    _gastoMensal.value = result.gasto
                }
                is ListaCompraResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao calcular gasto mensal"
                }
            }
            _isLoading.value = false
        }
    }
    
    // Calcular o valor da última lista
    fun calcularValorUltimaLista() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = listaUseCase.calcularValorUltimaLista()) {
                is ListaCompraResult.ValorUltimaListaSuccess -> {
                    _valorUltimaLista.value = result.valor
                }
                is ListaCompraResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao calcular valor da última lista"
                }
            }
            _isLoading.value = false
        }
    }
    
    // Carregar todas as listas
    fun carregarListas() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            // Como getAllListas() retorna Flow, precisamos de uma abordagem diferente
            // Por enquanto, vamos usar uma implementação simplificada
            calcularGastoMensal()
            calcularValorUltimaLista()
            
            _isLoading.value = false
        }
    }
    
    // Carregar listas ativas
    fun carregarListasAtivas() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = listaUseCase.getListasAtivas()) {
                is ListaCompraResult.ListasSuccess -> {
                    _listasAtivas.value = result.listas
                }
                is ListaCompraResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao carregar listas ativas"
                }
            }
            _isLoading.value = false
        }
    }
    
    // Carregar listas concluídas
    fun carregarListasConcluidas() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = listaUseCase.getListasConcluidas()) {
                is ListaCompraResult.ListasSuccess -> {
                    _listasConcluidas.value = result.listas
                }
                is ListaCompraResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao carregar listas concluídas"
                }
            }
            _isLoading.value = false
        }
    }
    
    // Marcar lista como concluída
    fun marcarListaComoConcluida(listaId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = listaUseCase.updateStatus(listaId, "CONCLUIDA")) {
                is ListaCompraResult.Success -> {
                    carregarListasAtivas()
                    carregarListasConcluidas()
                }
                is ListaCompraResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao marcar lista como concluída"
                }
            }
            _isLoading.value = false
        }
    }
    
    // Marcar lista como ativa
    fun marcarListaComoAtiva(listaId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = listaUseCase.updateStatus(listaId, "ATIVA")) {
                is ListaCompraResult.Success -> {
                    carregarListasAtivas()
                    carregarListasConcluidas()
                }
                is ListaCompraResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao marcar lista como ativa"
                }
            }
            _isLoading.value = false
        }
    }
    
    // Carregar estratégias de ordenação
    private fun carregarEstrategiasOrdenacao() {
        _estrategiasOrdenacao.value = itemUseCase.getEstrategiasOrdenacao()
    }
    
    // Carregar tipos de lista disponíveis
    private fun carregarTiposLista() {
        _tiposLista.value = listOf(
            ListaCompraType.Regular,
            ListaCompraType.Weekly,
            ListaCompraType.Monthly,
            ListaCompraType.Emergency,
            ListaCompraType.Recurrent
        )
    }
    
    // Limpar mensagem de erro
    fun limparErro() {
        _errorMessage.value = null
    }
    
    // Formatação usando a classe utilitária
    fun formatarValor(valor: Double): String {
        return ListaCompraFormatter.formatarValor(valor)
    }
    
    fun formatarData(timestamp: Long): String {
        return ListaCompraFormatter.formatDate(timestamp)
    }
    
    fun getMesAtualEmPortugues(): String {
        return ListaCompraFormatter.getMesAtualEmPortugues()
    }
}
