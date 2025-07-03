package com.lourenc.trolly.domain.usecase

import com.lourenc.trolly.data.local.entity.ShoppingList
import com.lourenc.trolly.domain.result.ShoppingListResult

interface ShoppingListUseCase {
    suspend fun createShoppingList(shoppingList: ShoppingList): ShoppingListResult
    suspend fun updateShoppingList(shoppingList: ShoppingList): ShoppingListResult
    suspend fun updateStatus(shoppingListId: Int, status: String): ShoppingListResult
    suspend fun deleteShoppingList(shoppingList: ShoppingList): ShoppingListResult
    suspend fun getShoppingListById(id: Int): ShoppingListResult
    suspend fun getAllShoppingLists(): ShoppingListResult
    suspend fun getActiveShoppingLists(): ShoppingListResult
    suspend fun getCompletedShoppingLists(): ShoppingListResult
    suspend fun getShoppingListsByMonth(month: Int, year: Int): ShoppingListResult
    suspend fun getShoppingListsByDateRange(startDate: Long, endDate: Long): ShoppingListResult
    suspend fun calculateMonthlyExpense(month: Int, year: Int): ShoppingListResult
    suspend fun calculateLastListValue(): ShoppingListResult
} 