package com.lourenc.trolly.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lourenc.trolly.domain.usecase.ShoppingListUseCase
import com.lourenc.trolly.domain.usecase.ListItemUseCase
import com.lourenc.trolly.domain.usecase.ProductSuggestionUseCase

class ShoppingListViewModelFactory(
    private val shoppingListUseCase: ShoppingListUseCase,
    private val listItemUseCase: ListItemUseCase,
    private val productSuggestionUseCase: ProductSuggestionUseCase? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingListViewModel::class.java)) {
            return ShoppingListViewModel(shoppingListUseCase, listItemUseCase, productSuggestionUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
