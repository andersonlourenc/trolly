package com.lourenc.trolly.domain.usecase

import com.lourenc.trolly.data.local.dao.ListItemDao
import com.lourenc.trolly.data.local.dao.ProductCooccurrence
import com.lourenc.trolly.data.local.dao.ProductFrequency
import com.lourenc.trolly.data.local.entity.ListItem
import com.lourenc.trolly.data.local.entity.MarketProduct
import com.lourenc.trolly.domain.repository.MarketProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

data class ProductSuggestion(
    val product: MarketProduct,
    val reason: String,
    val confidence: Float // 0.0 a 1.0
)

class ProductSuggestionUseCase(
    private val listItemDao: ListItemDao,
    private val marketProductRepository: MarketProductRepository
) {
    
    /**
     * Analisa o histórico de compras e sugere produtos que podem ter sido esquecidos
     */
    suspend fun getProductSuggestions(
        currentListItems: List<ListItem>,
        maxSuggestions: Int = 5
    ): List<ProductSuggestion> = withContext(Dispatchers.IO) {
        try {
            val suggestions = mutableListOf<ProductSuggestion>()
            
            // 1. Sugestões baseadas em coocorrência (produtos que costumam ser comprados juntos)
            val cooccurrenceSuggestions = getCooccurrenceSuggestions(currentListItems)
            suggestions.addAll(cooccurrenceSuggestions)
            
            // 2. Sugestões baseadas em frequência (produtos que o usuário compra frequentemente)
            val frequencySuggestions = getFrequencySuggestions(currentListItems)
            suggestions.addAll(frequencySuggestions)
            
            // 3. Sugestões baseadas em produtos recentes
            val recentSuggestions = getRecentSuggestions(currentListItems)
            suggestions.addAll(recentSuggestions)
            
            // Remover duplicatas e ordenar por confiança
            return@withContext suggestions
                .distinctBy { it.product.name }
                .sortedByDescending { it.confidence }
                .take(maxSuggestions)
                
        } catch (e: Exception) {
            println("Erro ao gerar sugestões: ${e.message}")
            return@withContext emptyList()
        }
    }
    
    /**
     * Sugestões baseadas em produtos que costumam ser comprados juntos
     */
    private suspend fun getCooccurrenceSuggestions(
        currentItems: List<ListItem>
    ): List<ProductSuggestion> {
        val currentProductNames = currentItems.map { it.name.lowercase() }.toSet()
        val cooccurrences = listItemDao.getProductCooccurrences()
        val suggestions = mutableListOf<ProductSuggestion>()
        
        for (cooccurrence in cooccurrences) {
            val product1 = cooccurrence.product1.lowercase()
            val product2 = cooccurrence.product2.lowercase()
            
            // Se um dos produtos está na lista atual, sugerir o outro
            if (currentProductNames.contains(product1) && !currentProductNames.contains(product2)) {
                val suggestedProduct = marketProductRepository.searchProducts(cooccurrence.product2).firstOrNull()
                if (suggestedProduct != null) {
                    val confidence = (cooccurrence.cooccurrence / 10.0f).coerceAtMost(0.9f)
                    suggestions.add(
                        ProductSuggestion(
                            product = suggestedProduct,
                            reason = "Frequentemente comprado com ${cooccurrence.product1}",
                            confidence = confidence
                        )
                    )
                }
            } else if (currentProductNames.contains(product2) && !currentProductNames.contains(product1)) {
                val suggestedProduct = marketProductRepository.searchProducts(cooccurrence.product1).firstOrNull()
                if (suggestedProduct != null) {
                    val confidence = (cooccurrence.cooccurrence / 10.0f).coerceAtMost(0.9f)
                    suggestions.add(
                        ProductSuggestion(
                            product = suggestedProduct,
                            reason = "Frequentemente comprado com ${cooccurrence.product2}",
                            confidence = confidence
                        )
                    )
                }
            }
        }
        
        return suggestions
    }
    
    /**
     * Sugestões baseadas em produtos que o usuário compra frequentemente
     */
    private suspend fun getFrequencySuggestions(
        currentItems: List<ListItem>
    ): List<ProductSuggestion> {
        val currentProductNames = currentItems.map { it.name.lowercase() }.toSet()
        val frequentProducts = listItemDao.getMostFrequentProducts()
        val suggestions = mutableListOf<ProductSuggestion>()
        
        for (frequentProduct in frequentProducts.take(10)) {
            if (!currentProductNames.contains(frequentProduct.name.lowercase())) {
                val product = marketProductRepository.searchProducts(frequentProduct.name).firstOrNull()
                if (product != null) {
                    val confidence = (frequentProduct.frequency / 20.0f).coerceAtMost(0.8f)
                    suggestions.add(
                        ProductSuggestion(
                            product = product,
                            reason = "Produto que você compra frequentemente",
                            confidence = confidence
                        )
                    )
                }
            }
        }
        
        return suggestions
    }
    
    /**
     * Sugestões baseadas em produtos comprados recentemente
     */
    private suspend fun getRecentSuggestions(
        currentItems: List<ListItem>
    ): List<ProductSuggestion> {
        val currentProductNames = currentItems.map { it.name.lowercase() }.toSet()
        
        // Produtos dos últimos 30 dias
        val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
        val recentFrequentProducts = listItemDao.getRecentFrequentProducts(thirtyDaysAgo)
        
        val suggestions = mutableListOf<ProductSuggestion>()
        
        for (recentProduct in recentFrequentProducts.take(5)) {
            if (!currentProductNames.contains(recentProduct.name.lowercase())) {
                val product = marketProductRepository.searchProducts(recentProduct.name).firstOrNull()
                if (product != null) {
                    val confidence = (recentProduct.frequency / 5.0f).coerceAtMost(0.7f)
                    suggestions.add(
                        ProductSuggestion(
                            product = product,
                            reason = "Comprado recentemente",
                            confidence = confidence
                        )
                    )
                }
            }
        }
        
        return suggestions
    }
    
    /**
     * Obtém estatísticas de padrões de compra para insights
     */
    suspend fun getPurchasePatterns(): Map<String, Any> = withContext(Dispatchers.IO) {
        try {
            val frequentProducts = listItemDao.getMostFrequentProducts()
            val cooccurrences = listItemDao.getProductCooccurrences()
            
            // Produtos dos últimos 7 dias
            val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
            val recentProducts = listItemDao.getRecentProducts(sevenDaysAgo)
            
            mapOf(
                "topProducts" to frequentProducts.take(5),
                "topCooccurrences" to cooccurrences.take(5),
                "recentProducts" to recentProducts.size,
                "totalPatterns" to cooccurrences.size
            )
        } catch (e: Exception) {
            println("Erro ao obter padrões: ${e.message}")
            emptyMap()
        }
    }
} 