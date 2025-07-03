package com.lourenc.trolly.di

import com.lourenc.trolly.data.local.dao.ListItemDao
import com.lourenc.trolly.data.local.dao.ShoppingListDao
import com.lourenc.trolly.data.repository.ListItemRepositoryImpl
import com.lourenc.trolly.data.repository.ShoppingListRepositoryImpl
import com.lourenc.trolly.domain.repository.ListItemRepository
import com.lourenc.trolly.domain.repository.ShoppingListRepository
import com.lourenc.trolly.domain.usecase.ListItemUseCase
import com.lourenc.trolly.domain.usecase.ShoppingListUseCase
import com.lourenc.trolly.domain.usecase.ListItemUseCaseImpl
import com.lourenc.trolly.domain.usecase.ShoppingListUseCaseImpl
import com.lourenc.trolly.presentation.viewmodel.ShoppingListViewModel
import com.lourenc.trolly.presentation.viewmodel.ShoppingListViewModelFactory

object AppModule {
    
    // Repositories
    fun provideShoppingListRepository(shoppingListDao: ShoppingListDao): ShoppingListRepository {
        return ShoppingListRepositoryImpl(shoppingListDao)
    }
    
    fun provideListItemRepository(listItemDao: ListItemDao): ListItemRepository {
        return ListItemRepositoryImpl(listItemDao)
    }
    

    
    // Use Cases
    fun provideShoppingListUseCase(
        shoppingListRepository: ShoppingListRepository,
        listItemRepository: ListItemRepository
    ): ShoppingListUseCase {
        return ShoppingListUseCaseImpl(shoppingListRepository, listItemRepository)
    }
    
    fun provideListItemUseCase(listItemRepository: ListItemRepository): ListItemUseCase {
        return ListItemUseCaseImpl(listItemRepository)
    }
    
    // ViewModel Factory
    fun provideShoppingListViewModelFactory(
        shoppingListUseCase: ShoppingListUseCase,
        listItemUseCase: ListItemUseCase
    ): ShoppingListViewModelFactory {
        return ShoppingListViewModelFactory(shoppingListUseCase, listItemUseCase)
    }
    
    // ViewModel
    fun provideShoppingListViewModel(
        shoppingListUseCase: ShoppingListUseCase,
        listItemUseCase: ListItemUseCase
    ): ShoppingListViewModel {
        return ShoppingListViewModel(shoppingListUseCase, listItemUseCase)
    }
} 