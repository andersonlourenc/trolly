package com.lourenc.trolly.domain.usecase

import com.lourenc.trolly.data.local.entity.ListItem
import com.lourenc.trolly.data.local.entity.MarketProduct
import com.lourenc.trolly.domain.repository.ListItemRepository
import com.lourenc.trolly.domain.result.ListItemResult
import com.lourenc.trolly.domain.usecase.ListItemUseCase

class ListItemUseCaseImpl(
    private val itemRepository: ListItemRepository
) : ListItemUseCase {

    override suspend fun addItem(item: ListItem): ListItemResult {
        return try {
            itemRepository.insertItem(item)
            ListItemResult.ItemSuccess(item)
        } catch (e: Exception) {
            ListItemResult.Error("Error adding item: ${e.message}", e)
        }
    }

    override suspend fun addOrIncrementItem(item: ListItem): ListItemResult {
        return try {
            val itemResult = itemRepository.addOrIncrementItem(item)
            ListItemResult.ItemSuccess(itemResult)
        } catch (e: Exception) {
            ListItemResult.Error("Error adding or incrementing item: ${e.message}", e)
        }
    }

    override suspend fun updateItem(item: ListItem): ListItemResult {
        return try {
            itemRepository.updateItem(item)
            ListItemResult.ItemSuccess(item)
        } catch (e: Exception) {
            ListItemResult.Error("Error updating item: ${e.message}", e)
        }
    }

    override suspend fun removeItem(item: ListItem): ListItemResult {
        return try {
            itemRepository.deleteItem(item)
            ListItemResult.Success("Item removed successfully")
        } catch (e: Exception) {
            ListItemResult.Error("Error removing item: ${e.message}", e)
        }
    }

    override suspend fun getItemsByList(listId: Int): ListItemResult {
        return try {
            val items = itemRepository.getItemsByList(listId)
            ListItemResult.ItemsSuccess(items)
        } catch (e: Exception) {
            ListItemResult.Error("Error loading items: ${e.message}", e)
        }
    }

    override suspend fun markItemAsPurchased(itemId: Int, purchased: Boolean): ListItemResult {
        return try {
            itemRepository.markItemAsPurchased(itemId, purchased)
            ListItemResult.Success("Item marked as ${if (purchased) "purchased" else "not purchased"}")
        } catch (e: Exception) {
            ListItemResult.Error("Error marking item: ${e.message}", e)
        }
    }

    override suspend fun calculateListTotal(listId: Int): ListItemResult {
        return try {
            val total = itemRepository.calculateListTotal(listId)
            ListItemResult.ListTotalSuccess(total)
        } catch (e: Exception) {
            ListItemResult.Error("Error calculating total: ${e.message}", e)
        }
    }

    override suspend fun getPurchasedItems(listId: Int): ListItemResult {
        return try {
            val items = itemRepository.getPurchasedItems(listId)
            ListItemResult.ItemsSuccess(items)
        } catch (e: Exception) {
            ListItemResult.Error("Error loading purchased items: ${e.message}", e)
        }
    }

    override suspend fun getUnpurchasedItems(listId: Int): ListItemResult {
        return try {
            val items = itemRepository.getUnpurchasedItems(listId)
            ListItemResult.ItemsSuccess(items)
        } catch (e: Exception) {
            ListItemResult.Error("Error loading unpurchased items: ${e.message}", e)
        }
    }

    override fun searchProducts(term: String): List<MarketProduct> {
        return itemRepository.filterProducts(term)
    }

    override fun convertProductToItem(product: MarketProduct, listId: Int, quantity: Int): ListItem {
        return ListItem(
            shoppingListId = listId,
            name = product.name,
            quantity = quantity,
            unit = product.unit,
            unitPrice = product.price,
            purchased = false
        )
    }
} 