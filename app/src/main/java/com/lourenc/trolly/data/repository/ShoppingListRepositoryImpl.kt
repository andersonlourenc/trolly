package com.lourenc.trolly.data.repository

import com.lourenc.trolly.data.local.dao.ShoppingListDao
import com.lourenc.trolly.data.local.entity.ShoppingList
import com.lourenc.trolly.domain.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class ShoppingListRepositoryImpl(private val shoppingListDao: ShoppingListDao) : ShoppingListRepository {

    override suspend fun insertShoppingList(shoppingList: ShoppingList) {
        try {
            println("DEBUG: Repository - Inserting shopping list: ${shoppingList.name}")
            shoppingListDao.insert(shoppingList)
            println("DEBUG: Repository - Shopping list inserted successfully")
        } catch (e: Exception) {
            println("DEBUG: Repository - Error inserting shopping list: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    override fun getAllShoppingLists(): Flow<List<ShoppingList>> {
        return shoppingListDao.getAll()
    }

    override fun getActiveShoppingLists(): Flow<List<ShoppingList>> {
        return shoppingListDao.getActiveLists()
    }

    override fun getCompletedShoppingLists(): Flow<List<ShoppingList>> {
        return shoppingListDao.getCompletedLists()
    }

    override suspend fun deleteShoppingList(shoppingList: ShoppingList) {
        shoppingListDao.delete(shoppingList)
    }

    override suspend fun updateShoppingList(shoppingList: ShoppingList) {
        shoppingListDao.update(shoppingList)
    }

    override suspend fun updateStatus(shoppingListId: Int, status: String) {
        shoppingListDao.updateStatus(shoppingListId, status)
    }

    override suspend fun getShoppingListById(id: Int): ShoppingList? {
        return shoppingListDao.getById(id)
    }

    override suspend fun getShoppingListsByMonth(month: Int, year: Int): List<ShoppingList> {
        // Implement as needed
        return emptyList()
    }

    override suspend fun getShoppingListsByDateRange(startDate: Long, endDate: Long): List<ShoppingList> {
        return shoppingListDao.getByDateRange(startDate, endDate)
    }
} 