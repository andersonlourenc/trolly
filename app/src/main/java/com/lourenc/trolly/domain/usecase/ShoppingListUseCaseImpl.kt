package com.lourenc.trolly.domain.usecase

import com.lourenc.trolly.data.local.entity.ShoppingList
import com.lourenc.trolly.domain.repository.ListItemRepository
import com.lourenc.trolly.domain.repository.ShoppingListRepository
import com.lourenc.trolly.domain.result.ShoppingListResult
import com.lourenc.trolly.domain.usecase.ShoppingListUseCase
import kotlinx.coroutines.flow.first
import java.util.Calendar

class ShoppingListUseCaseImpl(
    private val shoppingListRepository: ShoppingListRepository,
    private val listItemRepository: ListItemRepository
) : ShoppingListUseCase {

    override suspend fun createShoppingList(list: ShoppingList): ShoppingListResult {
        return try {
            println("DEBUG: UseCase - Creating shopping list: ${list.name}")
            shoppingListRepository.insertShoppingList(list)
            println("DEBUG: UseCase - Shopping list inserted successfully")
            ShoppingListResult.ShoppingListSuccess(list)
        } catch (e: Exception) {
            println("DEBUG: UseCase - Error creating shopping list: ${e.message}")
            e.printStackTrace()
            ShoppingListResult.Error("Error creating shopping list: ${e.message}", e)
        }
    }

    override suspend fun updateShoppingList(list: ShoppingList): ShoppingListResult {
        return try {
            shoppingListRepository.updateShoppingList(list)
            ShoppingListResult.ShoppingListSuccess(list)
        } catch (e: Exception) {
            ShoppingListResult.Error("Error updating shopping list: ${e.message}", e)
        }
    }

    override suspend fun updateStatus(listId: Int, status: String): ShoppingListResult {
        return try {
            shoppingListRepository.updateStatus(listId, status)
            ShoppingListResult.Success("Status updated successfully")
        } catch (e: Exception) {
            ShoppingListResult.Error("Error updating status: ${e.message}", e)
        }
    }

    override suspend fun deleteShoppingList(list: ShoppingList): ShoppingListResult {
        return try {
            shoppingListRepository.deleteShoppingList(list)
            ShoppingListResult.Success("Shopping list deleted successfully")
        } catch (e: Exception) {
            ShoppingListResult.Error("Error deleting shopping list: ${e.message}", e)
        }
    }

    override suspend fun getShoppingListById(id: Int): ShoppingListResult {
        return try {
            val list = shoppingListRepository.getShoppingListById(id)
            if (list != null) {
                ShoppingListResult.ShoppingListSuccess(list)
            } else {
                ShoppingListResult.Error("Shopping list not found")
            }
        } catch (e: Exception) {
            ShoppingListResult.Error("Error fetching shopping list: ${e.message}", e)
        }
    }

    override suspend fun getAllShoppingLists(): ShoppingListResult {
        return try {
            val lists = shoppingListRepository.getAllShoppingLists().first()
            ShoppingListResult.ShoppingListsSuccess(lists)
        } catch (e: Exception) {
            ShoppingListResult.Error("Error loading shopping lists: ${e.message}", e)
        }
    }

    override suspend fun getActiveShoppingLists(): ShoppingListResult {
        return try {
            val lists = shoppingListRepository.getActiveShoppingLists().first()
            ShoppingListResult.ShoppingListsSuccess(lists)
        } catch (e: Exception) {
            ShoppingListResult.Error("Error loading active shopping lists: ${e.message}", e)
        }
    }

    override suspend fun getCompletedShoppingLists(): ShoppingListResult {
        return try {
            val lists = shoppingListRepository.getCompletedShoppingLists().first()
            ShoppingListResult.ShoppingListsSuccess(lists)
        } catch (e: Exception) {
            ShoppingListResult.Error("Error loading completed shopping lists: ${e.message}", e)
        }
    }

    override suspend fun getShoppingListsByMonth(month: Int, year: Int): ShoppingListResult {
        return try {
            val lists = shoppingListRepository.getShoppingListsByMonth(month, year)
            ShoppingListResult.ShoppingListsSuccess(lists)
        } catch (e: Exception) {
            ShoppingListResult.Error("Error fetching shopping lists by month: ${e.message}", e)
        }
    }

    override suspend fun getShoppingListsByDateRange(startDate: Long, endDate: Long): ShoppingListResult {
        return try {
            val lists = shoppingListRepository.getShoppingListsByDateRange(startDate, endDate)
            ShoppingListResult.ShoppingListsSuccess(lists)
        } catch (e: Exception) {
            ShoppingListResult.Error("Error fetching shopping lists by date range: ${e.message}", e)
        }
    }

    override suspend fun calculateMonthlyExpense(month: Int, year: Int): ShoppingListResult {
        return try {
            println("DEBUG: Calculating monthly expense for $month/$year")
            val lists = shoppingListRepository.getCompletedShoppingLists().first()
            println("DEBUG: Total completed lists found: ${lists.size}")
            val calendar = Calendar.getInstance()
            calendar.set(year, month, 1, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startDate = calendar.timeInMillis
            calendar.add(Calendar.MONTH, 1)
            calendar.add(Calendar.MILLISECOND, -1)
            val endDate = calendar.timeInMillis
            println("DEBUG: Search period: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(startDate))} to ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(endDate))}")
            val completedListsOfMonth = lists.filter { list ->
                val creationDate = Calendar.getInstance().apply { timeInMillis = list.creationDate }
                val creationMonth = creationDate.get(Calendar.MONTH)
                val creationYear = creationDate.get(Calendar.YEAR)
                val isInMonth = creationMonth == month && creationYear == year
                println("DEBUG: List '${list.name}' created on ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(list.creationDate))} (Month: $creationMonth, Year: $creationYear) - Is in $month/$year: $isInMonth")
                isInMonth
            }
            println("DEBUG: Completed lists of the month: ${completedListsOfMonth.size}")
            var totalExpense = 0.0
            for (list in completedListsOfMonth) {
                val total = listItemRepository.calculateListTotal(list.id)
                println("DEBUG: List '${list.name}' (ID: ${list.id}) - Total: R$ $total")
                totalExpense += total
            }
            println("DEBUG: Total monthly expense: R$ $totalExpense")
            ShoppingListResult.MonthlyExpenseSuccess(totalExpense)
        } catch (e: Exception) {
            println("DEBUG: Error calculating monthly expense: ${e.message}")
            e.printStackTrace()
            ShoppingListResult.Error("Error calculating monthly expense: ${e.message}", e)
        }
    }

    override suspend fun calculateLastListValue(): ShoppingListResult {
        return try {
            println("DEBUG: Calculating last list value")
            val completedLists = shoppingListRepository.getCompletedShoppingLists().first()
            println("DEBUG: Total completed lists: ${completedLists.size}")
            val recentList = if (completedLists.isNotEmpty()) {
                completedLists.maxBy { it.creationDate }
            } else null
            if (recentList != null) {
                println("DEBUG: Most recent list: '${recentList.name}' created on ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(recentList.creationDate))}")
                val value = listItemRepository.calculateListTotal(recentList.id)
                println("DEBUG: Last list value: R$ $value")
                ShoppingListResult.LastListValueSuccess(value)
            } else {
                println("DEBUG: No completed list found")
                ShoppingListResult.LastListValueSuccess(0.0)
            }
        } catch (e: Exception) {
            println("DEBUG: Error calculating last list value: ${e.message}")
            e.printStackTrace()
            ShoppingListResult.Error("Error calculating last list value: ${e.message}", e)
        }
    }
} 