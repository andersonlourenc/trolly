package com.lourenc.trolly.domain.repository

import com.lourenc.trolly.data.local.entity.ListItem
import com.lourenc.trolly.data.local.entity.MarketProduct

interface ListItemRepository {
    suspend fun insertItem(item: ListItem)
    suspend fun updateItem(item: ListItem)
    suspend fun deleteItem(item: ListItem)
    suspend fun getItemsByList(listId: Int): List<ListItem>
    suspend fun getPurchasedItems(listId: Int): List<ListItem>
    suspend fun getUnpurchasedItems(listId: Int): List<ListItem>
    suspend fun markItemAsPurchased(itemId: Int, purchased: Boolean)
    suspend fun calculateListTotal(listId: Int): Double
    suspend fun addOrIncrementItem(item: ListItem): ListItem
    suspend fun getPredefinedProducts(): List<MarketProduct>
    suspend fun filterProducts(term: String): List<MarketProduct>
} 