package com.lourenc.trolly.data.repository

import com.lourenc.trolly.data.local.dao.ListItemDao
import com.lourenc.trolly.data.local.entity.ListItem
import com.lourenc.trolly.data.local.entity.MarketProduct
import com.lourenc.trolly.domain.repository.ListItemRepository
import com.lourenc.trolly.domain.repository.MarketProductRepository

class ListItemRepositoryImpl(
    private val itemDao: ListItemDao,
    private val marketProductRepository: MarketProductRepository
) : ListItemRepository {
    
    override suspend fun insertItem(item: ListItem) {
        itemDao.insert(item)
    }
    
    override suspend fun updateItem(item: ListItem) {
        itemDao.update(item)
    }
    
    override suspend fun deleteItem(item: ListItem) {
        itemDao.delete(item)
    }
    
    override suspend fun addOrIncrementItem(item: ListItem): ListItem {
        val existingItem = itemDao.getItemByName(item.shoppingListId, item.name)
        return if (existingItem != null) {
            val updatedItem = existingItem.copy(
                quantity = existingItem.quantity + item.quantity
            )
            itemDao.update(updatedItem)
            updatedItem
        } else {
            itemDao.insert(item)
            item
        }
    }
    
    override suspend fun getItemsByList(listId: Int): List<ListItem> {
        return itemDao.getItemsByList(listId)
    }
    
    override suspend fun getPurchasedItems(listId: Int): List<ListItem> {
        return itemDao.getItemsByList(listId).filter { it.purchased }
    }
    
    override suspend fun getUnpurchasedItems(listId: Int): List<ListItem> {
        return itemDao.getItemsByList(listId).filter { !it.purchased }
    }
    
    override suspend fun markItemAsPurchased(itemId: Int, purchased: Boolean) {
        // Implement when DAO has this method
        // itemDao.markAsPurchased(itemId, purchased)
    }
    
    override suspend fun calculateListTotal(listId: Int): Double {
        val items = itemDao.getItemsByList(listId)
        return items.sumOf { it.unitPrice * it.quantity }
    }
    
    override suspend fun getPredefinedProducts(): List<MarketProduct> {
        return marketProductRepository.getAllProducts()
    }
    
    override suspend fun filterProducts(term: String): List<MarketProduct> {
        return marketProductRepository.searchProducts(term)
    }
} 