package com.lourenc.trolly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.lourenc.trolly.data.local.entity.ItemLista
import com.lourenc.trolly.data.local.entity.ListaCompra
import com.lourenc.trolly.data.repository.ItemListaRepository
import com.lourenc.trolly.data.repository.ListaCompraRepository
import com.lourenc.trolly.data.repository.ProdutoMercado
import java.util.Calendar

class ListaCompraViewModel(
    private val repository: ListaCompraRepository,
    private val itemRepository: ItemListaRepository? = null
) : ViewModel() {

    val todasListas: LiveData<List<ListaCompra>> = repository.getAllListas().asLiveData()
    
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
    
    // Funções para gerenciar itens da lista
    fun carregarItensLista(listaId: Int) {
        itemRepository?.let { repo ->
            viewModelScope.launch {
                val itens = repo.getItensPorLista(listaId)
                _itensLista.value = itens
            }
        }
    }
    
    fun adicionarItemLista(item: ItemLista) {
        itemRepository?.let { repo ->
            viewModelScope.launch {
                repo.inserirItem(item)
                // Atualizar a lista após adicionar o item
                _itensLista.value = repo.getItensPorLista(item.idLista)
            }
        }
    }
    
    fun atualizarItemLista(item: ItemLista) {
        itemRepository?.let { repo ->
            viewModelScope.launch {
                repo.atualizarItem(item)
                // Atualizar a lista após editar o item
                _itensLista.value = repo.getItensPorLista(item.idLista)
            }
        }
    }
    
    fun removerItemLista(item: ItemLista) {
        itemRepository?.let { repo ->
            viewModelScope.launch {
                repo.deletarItem(item)
                // Atualizar a lista após remover o item
                _itensLista.value = repo.getItensPorLista(item.idLista)
            }
        }
    }
    
    // Pesquisar produtos
    fun pesquisarProdutos(termo: String) {
        itemRepository?.let { repo ->
            _produtosFiltrados.value = repo.filtrarProdutos(termo)
        }
    }
    
    // Converter produto para item de lista
    fun converterProdutoParaItem(produto: ProdutoMercado, listaId: Int, quantidade: Int = 1): ItemLista {
        return ItemLista(
            idLista = listaId,
            name = produto.nome,
            quantidade = quantidade,
            unidade = produto.unidade,
            precoUnitario = produto.preco,
            comprado = false
        )
    }
    
    // Calcular o gasto mensal
    fun calcularGastoMensal() {
        viewModelScope.launch {
            val todasListasValue = todasListas.value ?: emptyList()
            if (todasListasValue.isEmpty()) {
                _gastoMensal.value = 0.0
                return@launch
            }
            
            val calendar = Calendar.getInstance()
            val mesAtual = calendar.get(Calendar.MONTH)
            val anoAtual = calendar.get(Calendar.YEAR)
            
            var gastoTotal = 0.0
            
            for (lista in todasListasValue) {
                val dataLista = Calendar.getInstance()
                dataLista.timeInMillis = lista.dataCriacao
                
                // Verificar se a lista é do mês atual
                if (dataLista.get(Calendar.MONTH) == mesAtual && 
                    dataLista.get(Calendar.YEAR) == anoAtual) {
                    
                    itemRepository?.let { repo ->
                        val itens = repo.getItensPorLista(lista.id)
                        for (item in itens) {
                            gastoTotal += item.precoUnitario * item.quantidade
                        }
                    }
                }
            }
            
            _gastoMensal.value = gastoTotal
        }
    }
    
    // Calcular o valor da última lista
    fun calcularValorUltimaLista() {
        viewModelScope.launch {
            val todasListasValue = todasListas.value ?: emptyList()
            if (todasListasValue.isEmpty()) {
                _valorUltimaLista.value = 0.0
                return@launch
            }
            
            // Encontrar a lista mais recente
            val listaRecente = todasListasValue.maxByOrNull { it.dataCriacao }
            
            if (listaRecente != null) {
                itemRepository?.let { repo ->
                    val itens = repo.getItensPorLista(listaRecente.id)
                    var valorTotal = 0.0
                    
                    for (item in itens) {
                        valorTotal += item.precoUnitario * item.quantidade
                    }
                    
                    _valorUltimaLista.value = valorTotal
                }
            }
        }
    }
}
