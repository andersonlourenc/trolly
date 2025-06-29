package com.lourenc.trolly.di

import com.lourenc.trolly.data.local.dao.ItemListaDao
import com.lourenc.trolly.data.local.dao.ListaCompraDao
import com.lourenc.trolly.data.repository.impl.ItemListaRepositoryImpl
import com.lourenc.trolly.data.repository.impl.ListaCompraRepositoryImpl
import com.lourenc.trolly.data.repository.interfaces.ItemListaRepository
import com.lourenc.trolly.data.repository.interfaces.ListaCompraRepository
import com.lourenc.trolly.domain.factory.ListaCompraFactory
import com.lourenc.trolly.domain.factory.ListaCompraFactoryImpl
import com.lourenc.trolly.domain.strategy.*
import com.lourenc.trolly.domain.usecase.ItemListaUseCase
import com.lourenc.trolly.domain.usecase.ListaCompraUseCase
import com.lourenc.trolly.domain.usecase.impl.ItemListaUseCaseImpl
import com.lourenc.trolly.domain.usecase.impl.ListaCompraUseCaseImpl
import com.lourenc.trolly.viewmodel.ListaCompraViewModel
import com.lourenc.trolly.viewmodel.ListaCompraViewModelFactory

object AppModule {
    
    // Repositórios
    fun provideListaCompraRepository(listaDao: ListaCompraDao): ListaCompraRepository {
        return ListaCompraRepositoryImpl(listaDao)
    }
    
    fun provideItemListaRepository(itemDao: ItemListaDao): ItemListaRepository {
        return ItemListaRepositoryImpl(itemDao)
    }
    
    // Factory
    fun provideListaCompraFactory(): ListaCompraFactory {
        return ListaCompraFactoryImpl()
    }
    
    // Strategies
    fun provideAlphabeticalSortStrategy(): AlphabeticalSortStrategy {
        return AlphabeticalSortStrategy()
    }
    
    fun providePriceSortStrategy(): PriceSortStrategy {
        return PriceSortStrategy()
    }
    
    fun providePriceDescendingSortStrategy(): PriceDescendingSortStrategy {
        return PriceDescendingSortStrategy()
    }
    
    fun provideQuantitySortStrategy(): QuantitySortStrategy {
        return QuantitySortStrategy()
    }
    
    fun provideTotalPriceSortStrategy(): TotalPriceSortStrategy {
        return TotalPriceSortStrategy()
    }
    
    fun provideStatusSortStrategy(): StatusSortStrategy {
        return StatusSortStrategy()
    }
    
    fun provideCategorySortStrategy(): CategorySortStrategy {
        return CategorySortStrategy()
    }
    
    fun provideAllSortStrategies(): List<ItemSortingStrategy> {
        return listOf(
            provideAlphabeticalSortStrategy(),
            providePriceSortStrategy(),
            providePriceDescendingSortStrategy(),
            provideQuantitySortStrategy(),
            provideTotalPriceSortStrategy(),
            provideStatusSortStrategy(),
            provideCategorySortStrategy()
        )
    }
    
    // Use Cases
    fun provideListaCompraUseCase(
        listaRepository: ListaCompraRepository,
        itemRepository: ItemListaRepository,
        factory: ListaCompraFactory = provideListaCompraFactory()
    ): ListaCompraUseCase {
        return ListaCompraUseCaseImpl(listaRepository, itemRepository, factory)
    }
    
    fun provideItemListaUseCase(itemRepository: ItemListaRepository): ItemListaUseCase {
        return ItemListaUseCaseImpl(itemRepository)
    }
    
    // ViewModel Factory
    fun provideListaCompraViewModelFactory(
        listaUseCase: ListaCompraUseCase,
        itemUseCase: ItemListaUseCase
    ): ListaCompraViewModelFactory {
        return ListaCompraViewModelFactory(listaUseCase, itemUseCase)
    }
    
    // ViewModel
    fun provideListaCompraViewModel(
        listaUseCase: ListaCompraUseCase,
        itemUseCase: ItemListaUseCase
    ): ListaCompraViewModel {
        return ListaCompraViewModel(listaUseCase, itemUseCase)
    }
} 