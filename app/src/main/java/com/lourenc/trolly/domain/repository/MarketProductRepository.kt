package com.lourenc.trolly.domain.repository

import com.lourenc.trolly.data.local.entity.MarketProduct

interface MarketProductRepository {
    suspend fun insertProduct(product: MarketProduct)
    suspend fun insertAllProducts(products: List<MarketProduct>)
    suspend fun updateProduct(product: MarketProduct)
    suspend fun deleteProduct(product: MarketProduct)
    suspend fun getAllProducts(): List<MarketProduct>
    suspend fun searchProducts(term: String): List<MarketProduct>
    suspend fun getProductById(id: Int): MarketProduct?
    suspend fun getProductCount(): Int
    suspend fun deleteAllProducts()
    suspend fun initializeDefaultProducts()
} 