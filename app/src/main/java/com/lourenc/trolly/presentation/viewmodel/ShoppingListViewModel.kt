package com.lourenc.trolly.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.lourenc.trolly.data.local.entity.ShoppingList
import com.lourenc.trolly.data.local.entity.ListItem
import com.lourenc.trolly.data.local.entity.MarketProduct
import com.lourenc.trolly.domain.result.ListItemResult
import com.lourenc.trolly.domain.result.ShoppingListResult
import com.lourenc.trolly.domain.usecase.ListItemUseCase
import com.lourenc.trolly.domain.usecase.ShoppingListUseCase
import com.lourenc.trolly.utils.ShoppingListFormatter
import java.util.Calendar

class ShoppingListViewModel(
    private val shoppingListUseCase: ShoppingListUseCase,
    private val listItemUseCase: ListItemUseCase
) : ViewModel() {

    // LiveData para listas
    private val _shoppingLists = MutableLiveData<List<ShoppingList>>(emptyList())
    val shoppingLists: LiveData<List<ShoppingList>> = _shoppingLists
    
    // LiveData para listas ativas
    private val _activeLists = MutableLiveData<List<ShoppingList>>(emptyList())
    val activeLists: LiveData<List<ShoppingList>> = _activeLists
    
    // LiveData para listas concluídas
    private val _completedLists = MutableLiveData<List<ShoppingList>>(emptyList())
    val completedLists: LiveData<List<ShoppingList>> = _completedLists
    
    // LiveData para os itens da lista atual
    private val _currentListItems = MutableLiveData<List<ListItem>>(emptyList())
    val currentListItems: LiveData<List<ListItem>> = _currentListItems
    
    // LiveData para produtos filtrados na pesquisa
    private val _filteredProducts = MutableLiveData<List<MarketProduct>>(emptyList())
    val filteredProducts: LiveData<List<MarketProduct>> = _filteredProducts
    
    // LiveData para o gasto mensal
    private val _monthlyExpense = MutableLiveData<Double>(0.0)
    val monthlyExpense: LiveData<Double> = _monthlyExpense
    
    // LiveData para o valor da última lista
    private val _lastListValue = MutableLiveData<Double>(0.0)
    val lastListValue: LiveData<Double> = _lastListValue

    // LiveData para estados de loading e erro
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage
    
    init {
        // Inicialização do ViewModel
    }

    // Métodos para gerenciar listas
    fun addShoppingList(list: ShoppingList) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                println("DEBUG: Iniciando criação da lista: ${list.name}")
                when (val result = shoppingListUseCase.createShoppingList(list)) {
                    is ShoppingListResult.ShoppingListSuccess -> {
                        println("DEBUG: Lista criada com sucesso: ${result.shoppingList.id}")
                        loadActiveLists()
                        loadCompletedLists()
                    }
                    is ShoppingListResult.Error -> {
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
    


    fun deleteShoppingList(list: ShoppingList) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = shoppingListUseCase.deleteShoppingList(list)) {
                is ShoppingListResult.Success -> {
                    loadLists()
                }
                is ShoppingListResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao deletar lista"
                }
            }
            _isLoading.value = false
        }
    }

    fun updateShoppingList(list: ShoppingList) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = shoppingListUseCase.updateShoppingList(list)) {
                is ShoppingListResult.ShoppingListSuccess -> {
                    loadLists()
                }
                is ShoppingListResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao atualizar lista"
                }
            }
            _isLoading.value = false
        }
    }

    suspend fun getShoppingListById(id: Int): ShoppingList? {
        return when (val result = shoppingListUseCase.getShoppingListById(id)) {
            is ShoppingListResult.ShoppingListSuccess -> result.shoppingList
            is ShoppingListResult.Error -> {
                _errorMessage.value = result.message
                null
            }
            else -> null
        }
    }
    

    
    // Funções para gerenciar itens da lista
    fun loadListItems(listId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = listItemUseCase.getItemsByList(listId)) {
                is ListItemResult.ItemsSuccess -> {
                    _currentListItems.value = result.items
                }
                is ListItemResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao carregar itens"
                }
            }
            _isLoading.value = false
        }
    }
    

    
    fun addListItem(item: ListItem) {
            viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = listItemUseCase.addItem(item)) {
                is ListItemResult.ItemSuccess -> {
                    loadListItems(item.shoppingListId)
            }
                is ListItemResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao adicionar item"
                }
            }
            _isLoading.value = false
        }
    }
    
    fun addOrIncrementListItem(item: ListItem) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = listItemUseCase.addOrIncrementItem(item)) {
                is ListItemResult.ItemSuccess -> {
                    loadListItems(item.shoppingListId)
                }
                is ListItemResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao adicionar ou incrementar item"
                }
            }
            _isLoading.value = false
        }
    }
    
    fun updateListItem(item: ListItem) {
            viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = listItemUseCase.updateItem(item)) {
                is ListItemResult.ItemSuccess -> {
                    loadListItems(item.shoppingListId)
            }
                is ListItemResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao atualizar item"
                }
            }
            _isLoading.value = false
        }
    }
    
    fun removeListItem(item: ListItem) {
            viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = listItemUseCase.removeItem(item)) {
                is ListItemResult.Success -> {
                    loadListItems(item.shoppingListId)
            }
                is ListItemResult.Error -> {
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
    fun searchProducts(term: String) {
        viewModelScope.launch {
            try {
                val products = listItemUseCase.searchProducts(term)
                _filteredProducts.value = products
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao buscar produtos: ${e.message}"
            }
        }
    }
    
    // Converter produto para item de lista
    fun convertProductToItem(product: MarketProduct, listId: Int, quantity: Int = 1): ListItem {
        return listItemUseCase.convertProductToItem(product, listId, quantity)
    }
    
    // Calcular o gasto mensal
    fun calculateMonthlyExpense() {
        viewModelScope.launch {
            println("DEBUG: ViewModel - Iniciando cálculo do gasto mensal")
            _isLoading.value = true
            _errorMessage.value = null
            
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentYear = calendar.get(Calendar.YEAR)
            
            println("DEBUG: ViewModel - Mês atual: $currentMonth, Ano atual: $currentYear")
            
            when (val result = shoppingListUseCase.calculateMonthlyExpense(currentMonth, currentYear)) {
                is ShoppingListResult.MonthlyExpenseSuccess -> {
                    println("DEBUG: ViewModel - Gasto mensal calculado: R$ ${result.expense}")
                    _monthlyExpense.value = result.expense
                }
                is ShoppingListResult.Error -> {
                    println("DEBUG: ViewModel - Erro ao calcular gasto mensal: ${result.message}")
                    _errorMessage.value = result.message
                }
                else -> {
                    println("DEBUG: ViewModel - Resultado inesperado ao calcular gasto mensal")
                    _errorMessage.value = "Erro desconhecido ao calcular gasto mensal"
                }
            }
            _isLoading.value = false
        }
    }
    
    // Calcular o valor da última lista
    fun calculateLastListValue() {
        viewModelScope.launch {
            println("DEBUG: ViewModel - Iniciando cálculo do valor da última lista")
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = shoppingListUseCase.calculateLastListValue()) {
                is ShoppingListResult.LastListValueSuccess -> {
                    println("DEBUG: ViewModel - Valor da última lista calculado: R$ ${result.value}")
                    _lastListValue.value = result.value
                }
                is ShoppingListResult.Error -> {
                    println("DEBUG: ViewModel - Erro ao calcular valor da última lista: ${result.message}")
                    _errorMessage.value = result.message
                }
                else -> {
                    println("DEBUG: ViewModel - Resultado inesperado ao calcular valor da última lista")
                    _errorMessage.value = "Erro desconhecido ao calcular valor da última lista"
                }
            }
            _isLoading.value = false
        }
    }
    
    // Carregar todas as listas
    fun loadLists() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            // Carregar listas ativas e concluídas
            loadActiveLists()
            loadCompletedLists()
            
            // Calcular gasto mensal e valor da última lista
            calculateMonthlyExpense()
            calculateLastListValue()
            
            _isLoading.value = false
        }
    }
    
    // Carregar listas ativas
    fun loadActiveLists() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = shoppingListUseCase.getActiveShoppingLists()) {
                is ShoppingListResult.ShoppingListsSuccess -> {
                    _activeLists.value = result.shoppingLists
                }
                is ShoppingListResult.Error -> {
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
    fun loadCompletedLists() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = shoppingListUseCase.getCompletedShoppingLists()) {
                is ShoppingListResult.ShoppingListsSuccess -> {
                    _completedLists.value = result.shoppingLists
                }
                is ShoppingListResult.Error -> {
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
    fun markListAsCompleted(listId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = shoppingListUseCase.updateStatus(listId, "COMPLETED")) {
                is ShoppingListResult.Success -> {
                    loadActiveLists()
                    loadCompletedLists()
                }
                is ShoppingListResult.Error -> {
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
    fun markListAsActive(listId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = shoppingListUseCase.updateStatus(listId, "ACTIVE")) {
                is ShoppingListResult.Success -> {
                    loadActiveLists()
                    loadCompletedLists()
                }
                is ShoppingListResult.Error -> {
                    _errorMessage.value = result.message
                }
                else -> {
                    _errorMessage.value = "Erro desconhecido ao marcar lista como ativa"
                }
            }
            _isLoading.value = false
        }
    }
    

    
    // Limpar mensagem de erro
    fun clearError() {
        _errorMessage.value = null
    }
    
    // Formatação usando a classe utilitária
    fun formatValue(value: Double): String {
        return ShoppingListFormatter.formatValue(value)
    }
    
    fun formatValueWithDash(value: Double): String {
        return ShoppingListFormatter.formatValueWithDash(value)
    }
    
    fun formatDate(timestamp: Long): String {
        return ShoppingListFormatter.formatDate(timestamp)
    }
    
    fun getCurrentMonthInPortuguese(): String {
        return ShoppingListFormatter.getCurrentMonthInPortuguese()
    }
    
    // Verificar se há listas concluídas
    fun hasCompletedLists(): Boolean {
        return _completedLists.value?.isNotEmpty() == true
    }
    
    // Verificar se há gasto mensal
    fun hasMonthlyExpense(): Boolean {
        return _monthlyExpense.value != null && _monthlyExpense.value!! > 0.0
    }
    
    // Verificar se há valor da última lista
    fun hasLastListValue(): Boolean {
        return _lastListValue.value != null && _lastListValue.value!! > 0.0
    }
    
    // Calcular valor real de uma lista
    suspend fun calculateRealListValue(listId: Int): Double {
        return try {
            when (val result = listItemUseCase.calculateListTotal(listId)) {
                is ListItemResult.ListTotalSuccess -> result.total
                is ListItemResult.Error -> 0.0
                else -> 0.0
            }
        } catch (e: Exception) {
            0.0
        }
    }
    
    // Verificar se uma lista está concluída
    fun isListCompleted(list: ShoppingList): Boolean {
        return list.status == "COMPLETED"
    }
    
    // Formatar valor da lista baseado no status
    suspend fun formatListValue(list: ShoppingList): String {
        return if (isListCompleted(list)) {
            val realValue = calculateRealListValue(list.id)
            ShoppingListFormatter.formatValueWithDash(realValue)
        } else {
            "—"
        }
    }
    
    // Calcular valor real de uma lista (versão não-suspend para UI)
    fun calculateRealListValueAsync(listId: Int, onResult: (Double) -> Unit) {
        viewModelScope.launch {
            val value = calculateRealListValue(listId)
            onResult(value)
        }
    }
}
