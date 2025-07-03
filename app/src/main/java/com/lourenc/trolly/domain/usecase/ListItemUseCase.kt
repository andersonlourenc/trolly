package com.lourenc.trolly.domain.usecase

import com.lourenc.trolly.data.local.entity.ListItem
import com.lourenc.trolly.data.local.entity.MarketProduct
import com.lourenc.trolly.domain.result.ListItemResult

interface ListItemUseCase {
    suspend fun addItem(item: ListItem): ListItemResult
    suspend fun addOrIncrementItem(item: ListItem): ListItemResult
    suspend fun updateItem(item: ListItem): ListItemResult
    suspend fun removeItem(item: ListItem): ListItemResult
    suspend fun getItemsByList(listId: Int): ListItemResult
    suspend fun markItemAsPurchased(itemId: Int, purchased: Boolean): ListItemResult
    suspend fun calculateListTotal(listId: Int): ListItemResult
    suspend fun getPurchasedItems(listId: Int): ListItemResult
    suspend fun getUnpurchasedItems(listId: Int): ListItemResult
    fun searchProducts(term: String): List<MarketProduct>
    fun convertProductToItem(product: MarketProduct, listId: Int, quantity: Int = 1): ListItem
} 