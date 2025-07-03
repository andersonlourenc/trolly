package com.lourenc.trolly.domain.result

import com.lourenc.trolly.data.local.entity.ShoppingList

sealed class ShoppingListResult {
    data class Success(val data: Any) : ShoppingListResult()
    data class Error(val message: String, val exception: Exception? = null) : ShoppingListResult()
    object Loading : ShoppingListResult()
    
    // Resultados específicos
    data class ShoppingListSuccess(val shoppingList: ShoppingList) : ShoppingListResult()
    data class ShoppingListsSuccess(val shoppingLists: List<ShoppingList>) : ShoppingListResult()
    data class MonthlyExpenseSuccess(val expense: Double) : ShoppingListResult()
    data class LastListValueSuccess(val value: Double) : ShoppingListResult()
} 