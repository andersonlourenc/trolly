package com.lourenc.trolly.domain.strategy

import com.lourenc.trolly.data.local.entity.ItemLista

interface ItemSortingStrategy {
    fun sort(items: List<ItemLista>): List<ItemLista>
    val displayName: String
    val description: String
}

class AlphabeticalSortStrategy : ItemSortingStrategy {
    override fun sort(items: List<ItemLista>): List<ItemLista> {
        return items.sortedBy { it.name.lowercase() }
    }
    
    override val displayName: String = "Alfabética"
    override val description: String = "Ordenar por nome do item"
}

class PriceSortStrategy : ItemSortingStrategy {
    override fun sort(items: List<ItemLista>): List<ItemLista> {
        return items.sortedBy { it.precoUnitario }
    }
    
    override val displayName: String = "Preço"
    override val description: String = "Ordenar por preço (menor para maior)"
}

class PriceDescendingSortStrategy : ItemSortingStrategy {
    override fun sort(items: List<ItemLista>): List<ItemLista> {
        return items.sortedByDescending { it.precoUnitario }
    }
    
    override val displayName: String = "Preço (Maior)"
    override val description: String = "Ordenar por preço (maior para menor)"
}

class QuantitySortStrategy : ItemSortingStrategy {
    override fun sort(items: List<ItemLista>): List<ItemLista> {
        return items.sortedBy { it.quantidade }
    }
    
    override val displayName: String = "Quantidade"
    override val description: String = "Ordenar por quantidade"
}

class TotalPriceSortStrategy : ItemSortingStrategy {
    override fun sort(items: List<ItemLista>): List<ItemLista> {
        return items.sortedBy { it.precoUnitario * it.quantidade }
    }
    
    override val displayName: String = "Preço Total"
    override val description: String = "Ordenar por preço total (preço × quantidade)"
}

class StatusSortStrategy : ItemSortingStrategy {
    override fun sort(items: List<ItemLista>): List<ItemLista> {
        return items.sortedWith(
            compareBy<ItemLista> { it.comprado }
                .thenBy { it.name.lowercase() }
        )
    }
    
    override val displayName: String = "Status"
    override val description: String = "Ordenar por status (não comprado primeiro)"
}

class CategorySortStrategy : ItemSortingStrategy {
    override fun sort(items: List<ItemLista>): List<ItemLista> {
        // Simulação de categorias baseada no nome do item
        return items.sortedWith(
            compareBy<ItemLista> { getCategory(it.name) }
                .thenBy { it.name.lowercase() }
        )
    }
    
    override val displayName: String = "Categoria"
    override val description: String = "Ordenar por categoria do item"
    
    private fun getCategory(itemName: String): Int {
        val name = itemName.lowercase()
        return when {
            name.contains("fruta") || name.contains("verdura") || name.contains("legume") -> 1
            name.contains("carne") || name.contains("frango") || name.contains("peixe") -> 2
            name.contains("laticínio") || name.contains("queijo") || name.contains("leite") -> 3
            name.contains("pão") || name.contains("massa") || name.contains("arroz") -> 4
            name.contains("limpeza") || name.contains("sabão") || name.contains("detergente") -> 5
            name.contains("higiene") || name.contains("shampoo") || name.contains("sabonete") -> 6
            else -> 7
        }
    }
}

class ItemSorter(private val strategy: ItemSortingStrategy) {
    fun sortItems(items: List<ItemLista>): List<ItemLista> {
        return strategy.sort(items)
    }
    
    fun getStrategyInfo(): String {
        return "${strategy.displayName}: ${strategy.description}"
    }
} 