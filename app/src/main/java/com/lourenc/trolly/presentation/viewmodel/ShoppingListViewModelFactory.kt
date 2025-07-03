package com.lourenc.trolly.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lourenc.trolly.domain.usecase.ShoppingListUseCase
import com.lourenc.trolly.domain.usecase.ListItemUseCase

class ShoppingListViewModelFactory(
    private val shoppingListUseCase: ShoppingListUseCase,
    private val listItemUseCase: ListItemUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingListViewModel::class.java)) {
            return ShoppingListViewModel(shoppingListUseCase, listItemUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
