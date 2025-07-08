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
    
    // Queries para análise de padrões
    @Query("""
        SELECT name, COUNT(*) as frequency 
        FROM list_item 
        WHERE shoppingListId IN (
            SELECT id FROM shopping_list WHERE status = 'COMPLETED'
        )
        GROUP BY name 
        ORDER BY frequency DESC 
        LIMIT 20
    """)
    suspend fun getMostFrequentProducts(): List<ProductFrequency>
    
    @Query("""
        SELECT li1.name as product1, li2.name as product2, COUNT(*) as cooccurrence
        FROM list_item li1
        INNER JOIN list_item li2 ON li1.shoppingListId = li2.shoppingListId 
            AND li1.name < li2.name
        WHERE li1.shoppingListId IN (
            SELECT id FROM shopping_list WHERE status = 'COMPLETED'
        )
        GROUP BY li1.name, li2.name
        HAVING cooccurrence >= 2
        ORDER BY cooccurrence DESC
        LIMIT 50
    """)
    suspend fun getProductCooccurrences(): List<ProductCooccurrence>
    
    @Query("""
        SELECT DISTINCT name 
        FROM list_item 
        WHERE shoppingListId IN (
            SELECT id FROM shopping_list 
            WHERE status = 'COMPLETED' 
            AND creationDate >= :sinceDate
        )
        ORDER BY name
    """)
    suspend fun getRecentProducts(sinceDate: Long): List<String>
    
    @Query("""
        SELECT name, COUNT(*) as frequency
        FROM list_item 
        WHERE shoppingListId IN (
            SELECT id FROM shopping_list 
            WHERE status = 'COMPLETED' 
            AND creationDate >= :sinceDate
        )
        GROUP BY name
        ORDER BY frequency DESC
        LIMIT 10
    """)
    suspend fun getRecentFrequentProducts(sinceDate: Long): List<ProductFrequency>
}

// Data classes para resultados das queries
data class ProductFrequency(
    val name: String,
    val frequency: Int
)

data class ProductCooccurrence(
    val product1: String,
    val product2: String,
    val cooccurrence: Int
)
