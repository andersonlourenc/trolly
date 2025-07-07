package com.lourenc.trolly.domain.usecase

import com.lourenc.trolly.data.local.entity.ShoppingList
import com.lourenc.trolly.domain.result.ShoppingListResult
import java.util.Calendar

interface BulkListUseCase {
    suspend fun createListForDate(name: String, year: Int, month: Int, day: Int): ShoppingListResult
    suspend fun createWeeklyLists(startDate: Calendar, weeks: Int, baseName: String): ShoppingListResult
    suspend fun createMonthlyLists(startDate: Calendar, months: Int, baseName: String): ShoppingListResult
    suspend fun createListFromTemplate(templateName: String, targetDate: Calendar): ShoppingListResult
    suspend fun duplicateList(listId: Int, newDate: Calendar): ShoppingListResult
    suspend fun createListForYesterday(name: String): ShoppingListResult
    suspend fun createListForLastWeek(name: String): ShoppingListResult
    suspend fun createListForLastMonth(name: String): ShoppingListResult
} 