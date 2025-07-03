package com.lourenc.trolly.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lourenc.trolly.data.local.entity.ShoppingList

@Dao
interface ShoppingListDao {
    @Insert
    suspend fun insert(shoppingList: ShoppingList)

    @Update
    suspend fun update(shoppingList: ShoppingList)

    @Delete
    suspend fun delete(shoppingList: ShoppingList)

    @Query("SELECT * FROM shopping_list ORDER BY creationDate DESC")
    fun getAll(): kotlinx.coroutines.flow.Flow<List<ShoppingList>>

    @Query("SELECT * FROM shopping_list WHERE status = 'ACTIVE' ORDER BY creationDate DESC")
    fun getActiveLists(): kotlinx.coroutines.flow.Flow<List<ShoppingList>>

    @Query("SELECT * FROM shopping_list WHERE status = 'COMPLETED' ORDER BY creationDate DESC")
    fun getCompletedLists(): kotlinx.coroutines.flow.Flow<List<ShoppingList>>

    @Query("SELECT * FROM shopping_list WHERE id = :id")
    suspend fun getById(id: Int): ShoppingList?

    @Query("SELECT * FROM shopping_list WHERE creationDate BETWEEN :startDate AND :endDate ORDER BY creationDate DESC")
    suspend fun getByDateRange(startDate: Long, endDate: Long): List<ShoppingList>

    @Query("UPDATE shopping_list SET status = :status WHERE id = :shoppingListId")
    suspend fun updateStatus(shoppingListId: Int, status: String)
}
