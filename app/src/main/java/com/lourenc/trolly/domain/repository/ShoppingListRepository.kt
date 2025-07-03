package com.lourenc.trolly.domain.repository

import com.lourenc.trolly.data.local.entity.ShoppingList
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {
    fun getAllShoppingLists(): Flow<List<ShoppingList>>
    fun getActiveShoppingLists(): Flow<List<ShoppingList>>
    fun getCompletedShoppingLists(): Flow<List<ShoppingList>>
    suspend fun insertShoppingList(shoppingList: ShoppingList)
    suspend fun updateShoppingList(shoppingList: ShoppingList)
    suspend fun updateStatus(shoppingListId: Int, status: String)
    suspend fun deleteShoppingList(shoppingList: ShoppingList)
    suspend fun getShoppingListById(id: Int): ShoppingList?
    suspend fun getShoppingListsByMonth(month: Int, year: Int): List<ShoppingList>
    suspend fun getShoppingListsByDateRange(startDate: Long, endDate: Long): List<ShoppingList>
} 