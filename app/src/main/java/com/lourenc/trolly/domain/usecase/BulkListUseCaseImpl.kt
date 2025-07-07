package com.lourenc.trolly.domain.usecase

import com.lourenc.trolly.data.local.entity.ShoppingList
import com.lourenc.trolly.domain.builder.ShoppingListBuilder
import com.lourenc.trolly.domain.repository.ListItemRepository
import com.lourenc.trolly.domain.repository.ShoppingListRepository
import com.lourenc.trolly.domain.result.ShoppingListResult
import java.util.Calendar

class BulkListUseCaseImpl(
    private val shoppingListRepository: ShoppingListRepository,
    private val listItemRepository: ListItemRepository
) : BulkListUseCase {

    override suspend fun createListForDate(
        name: String, 
        year: Int, 
        month: Int, 
        day: Int
    ): ShoppingListResult {
        return try {
            val list = ShoppingListBuilder()
                .setName(name)
                .setCreationDate(year, month, day)
                .build()
            
            shoppingListRepository.insertShoppingList(list)
            ShoppingListResult.ShoppingListSuccess(list)
        } catch (e: Exception) {
            ShoppingListResult.Error("Erro ao criar lista para data: ${e.message}", e)
        }
    }

    override suspend fun createWeeklyLists(
        startDate: Calendar, 
        weeks: Int, 
        baseName: String
    ): ShoppingListResult {
        return try {
            val createdLists = mutableListOf<ShoppingList>()
            val calendar = startDate.clone() as Calendar
            
            for (i in 0 until weeks) {
                val list = ShoppingListBuilder()
                    .setName("$baseName - Semana ${i + 1}")
                    .setCreationDate(calendar.timeInMillis)
                    .build()
                
                shoppingListRepository.insertShoppingList(list)
                createdLists.add(list)
                
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
            }
            
            ShoppingListResult.ShoppingListsSuccess(createdLists)
        } catch (e: Exception) {
            ShoppingListResult.Error("Erro ao criar listas semanais: ${e.message}", e)
        }
    }

    override suspend fun createMonthlyLists(
        startDate: Calendar, 
        months: Int, 
        baseName: String
    ): ShoppingListResult {
        return try {
            val createdLists = mutableListOf<ShoppingList>()
            val calendar = startDate.clone() as Calendar
            
            for (i in 0 until months) {
                val list = ShoppingListBuilder()
                    .setName("$baseName - ${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}")
                    .setCreationDate(calendar.timeInMillis)
                    .build()
                
                shoppingListRepository.insertShoppingList(list)
                createdLists.add(list)
                
                calendar.add(Calendar.MONTH, 1)
            }
            
            ShoppingListResult.ShoppingListsSuccess(createdLists)
        } catch (e: Exception) {
            ShoppingListResult.Error("Erro ao criar listas mensais: ${e.message}", e)
        }
    }

    override suspend fun createListFromTemplate(
        templateName: String, 
        targetDate: Calendar
    ): ShoppingListResult {
        return try {
            // Buscar lista template (você pode criar algumas listas padrão)
            val templateLists = mapOf(
                "churrasco" to listOf("Carne", "Carvão", "Cerveja", "Pão de alho", "Farofa"),
                "feira" to listOf("Frutas", "Verduras", "Legumes", "Temperos"),
                "limpeza" to listOf("Detergente", "Sabão em pó", "Desinfetante", "Papel higiênico")
            )
            
            val template = templateLists[templateName.lowercase()]
            if (template != null) {
                val list = ShoppingListBuilder()
                    .setName("Lista de $templateName")
                    .setDescription("Lista criada automaticamente para $templateName")
                    .setCreationDate(targetDate.timeInMillis)
                    .build()
                
                shoppingListRepository.insertShoppingList(list)
                ShoppingListResult.ShoppingListSuccess(list)
            } else {
                ShoppingListResult.Error("Template '$templateName' não encontrado")
            }
        } catch (e: Exception) {
            ShoppingListResult.Error("Erro ao criar lista do template: ${e.message}", e)
        }
    }

    override suspend fun duplicateList(listId: Int, newDate: Calendar): ShoppingListResult {
        return try {
            val originalList = shoppingListRepository.getShoppingListById(listId)
            if (originalList != null) {
                val newList = ShoppingListBuilder()
                    .setName("${originalList.name} (Cópia)")
                    .setDescription(originalList.description)
                    .setCreationDate(newDate.timeInMillis)
                    .setEstimatedTotal(originalList.estimatedTotal)
                    .setCoverPhoto(originalList.coverPhoto)
                    .build()
                
                shoppingListRepository.insertShoppingList(newList)
                ShoppingListResult.ShoppingListSuccess(newList)
            } else {
                ShoppingListResult.Error("Lista original não encontrada")
            }
        } catch (e: Exception) {
            ShoppingListResult.Error("Erro ao duplicar lista: ${e.message}", e)
        }
    }

    override suspend fun createListForYesterday(name: String): ShoppingListResult {
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        return createListForDate(
            name,
            yesterday.get(Calendar.YEAR),
            yesterday.get(Calendar.MONTH),
            yesterday.get(Calendar.DAY_OF_MONTH)
        )
    }

    override suspend fun createListForLastWeek(name: String): ShoppingListResult {
        val lastWeek = Calendar.getInstance()
        lastWeek.add(Calendar.WEEK_OF_YEAR, -1)
        return createListForDate(
            name,
            lastWeek.get(Calendar.YEAR),
            lastWeek.get(Calendar.MONTH),
            lastWeek.get(Calendar.DAY_OF_MONTH)
        )
    }

    override suspend fun createListForLastMonth(name: String): ShoppingListResult {
        val lastMonth = Calendar.getInstance()
        lastMonth.add(Calendar.MONTH, -1)
        return createListForDate(
            name,
            lastMonth.get(Calendar.YEAR),
            lastMonth.get(Calendar.MONTH),
            lastMonth.get(Calendar.DAY_OF_MONTH)
        )
    }
} 