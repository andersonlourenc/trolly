package com.lourenc.trolly.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lourenc.trolly.data.local.entity.ListItem

@Dao
interface ListItemDao {
    @Insert
    suspend fun insert(item: ListItem)

    @Update
    suspend fun update(item: ListItem)

    @Delete
    suspend fun delete(item: ListItem)

    @Query("SELECT * FROM list_item WHERE shoppingListId = :shoppingListId")
    suspend fun getItemsByList(shoppingListId: Int): List<ListItem>
    
    @Query("SELECT * FROM list_item WHERE shoppingListId = :shoppingListId AND name = :name LIMIT 1")
    suspend fun getItemByName(shoppingListId: Int, name: String): ListItem?
}
