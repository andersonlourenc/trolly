package com.lourenc.trolly.domain.result

import com.lourenc.trolly.data.local.entity.ListItem

sealed class ListItemResult {
    data class Success(val data: Any) : ListItemResult()
    data class Error(val message: String, val exception: Exception? = null) : ListItemResult()
    object Loading : ListItemResult()
    
    // Resultados específicos
    data class ItemSuccess(val item: ListItem) : ListItemResult()
    data class ItemsSuccess(val items: List<ListItem>) : ListItemResult()
    data class ListTotalSuccess(val total: Double) : ListItemResult()
} 