package com.lourenc.trolly.data.local.dao

import androidx.room.*
import com.lourenc.trolly.data.local.entity.MarketProduct

@Dao
interface MarketProductDao {
    @Insert
    suspend fun insert(product: MarketProduct)

    @Insert
    suspend fun insertAll(products: List<MarketProduct>)

    @Update
    suspend fun update(product: MarketProduct)

    @Delete
    suspend fun delete(product: MarketProduct)

    @Query("SELECT * FROM market_product ORDER BY name ASC")
    suspend fun getAll(): List<MarketProduct>

    @Query("SELECT * FROM market_product WHERE name LIKE '%' || :term || '%' ORDER BY name ASC")
    suspend fun searchByName(term: String): List<MarketProduct>

    @Query("SELECT * FROM market_product WHERE id = :id")
    suspend fun getById(id: Int): MarketProduct?

    @Query("SELECT COUNT(*) FROM market_product")
    suspend fun getCount(): Int

    @Query("DELETE FROM market_product")
    suspend fun deleteAll()
} 