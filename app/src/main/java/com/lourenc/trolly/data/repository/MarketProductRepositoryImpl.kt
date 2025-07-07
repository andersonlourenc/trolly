package com.lourenc.trolly.data.repository

import com.lourenc.trolly.data.local.dao.MarketProductDao
import com.lourenc.trolly.data.local.entity.MarketProduct
import com.lourenc.trolly.domain.repository.MarketProductRepository

class MarketProductRepositoryImpl(private val marketProductDao: MarketProductDao) : MarketProductRepository {
    
    override suspend fun insertProduct(product: MarketProduct) {
        marketProductDao.insert(product)
    }
    
    override suspend fun insertAllProducts(products: List<MarketProduct>) {
        marketProductDao.insertAll(products)
    }
    
    override suspend fun updateProduct(product: MarketProduct) {
        marketProductDao.update(product)
    }
    
    override suspend fun deleteProduct(product: MarketProduct) {
        marketProductDao.delete(product)
    }
    
    override suspend fun getAllProducts(): List<MarketProduct> {
        return marketProductDao.getAll()
    }
    
    override suspend fun searchProducts(term: String): List<MarketProduct> {
        return marketProductDao.searchByName(term)
    }
    
    override suspend fun getProductById(id: Int): MarketProduct? {
        return marketProductDao.getById(id)
    }
    
    override suspend fun getProductCount(): Int {
        return marketProductDao.getCount()
    }
    
    override suspend fun deleteAllProducts() {
        marketProductDao.deleteAll()
    }
    
    override suspend fun initializeDefaultProducts() {
        val count = marketProductDao.getCount()
        if (count == 0) {
            val defaultProducts = listOf(
                MarketProduct(name = "Arroz", unit = "kg", price = 5.50),
                MarketProduct(name = "Feijão", unit = "kg", price = 8.00),
                MarketProduct(name = "Óleo de Soja", unit = "L", price = 9.50),
                MarketProduct(name = "Açúcar", unit = "kg", price = 4.50),
                MarketProduct(name = "Café", unit = "kg", price = 25.00),
                MarketProduct(name = "Leite", unit = "L", price = 6.00),
                MarketProduct(name = "Pão", unit = "un", price = 0.50),
                MarketProduct(name = "Manteiga", unit = "kg", price = 15.00),
                MarketProduct(name = "Queijo", unit = "kg", price = 35.00),
                MarketProduct(name = "Carne Bovina", unit = "kg", price = 45.00),
                MarketProduct(name = "Frango", unit = "kg", price = 12.00),
                MarketProduct(name = "Ovos", unit = "dz", price = 8.00),
                MarketProduct(name = "Tomate", unit = "kg", price = 6.00),
                MarketProduct(name = "Cebola", unit = "kg", price = 4.00),
                MarketProduct(name = "Batata", unit = "kg", price = 5.00),
                MarketProduct(name = "Banana", unit = "kg", price = 4.50),
                MarketProduct(name = "Maçã", unit = "kg", price = 8.00),
                MarketProduct(name = "Laranja", unit = "kg", price = 3.50),
                MarketProduct(name = "Detergente", unit = "un", price = 3.50),
                MarketProduct(name = "Sabão em Pó", unit = "kg", price = 12.00),
                MarketProduct(name = "Papel Higiênico", unit = "rolo", price = 0.80),
                MarketProduct(name = "Shampoo", unit = "un", price = 15.00),
                MarketProduct(name = "Sabonete", unit = "un", price = 2.50),
                MarketProduct(name = "Cerveja", unit = "lata", price = 4.50),
                MarketProduct(name = "Refrigerante", unit = "L", price = 8.00),
                MarketProduct(name = "Suco", unit = "L", price = 6.50),
                MarketProduct(name = "Água", unit = "L", price = 2.00),
                MarketProduct(name = "Biscoito", unit = "pacote", price = 3.50),
                MarketProduct(name = "Chocolate", unit = "barra", price = 5.00),
                MarketProduct(name = "Sal", unit = "kg", price = 2.00),
                MarketProduct(name = "Pimenta", unit = "kg", price = 25.00),
                MarketProduct(name = "Alho", unit = "kg", price = 15.00),
                MarketProduct(name = "Cenoura", unit = "kg", price = 3.50),
                MarketProduct(name = "Alface", unit = "un", price = 2.00),
                MarketProduct(name = "Couve", unit = "maço", price = 2.50),
                MarketProduct(name = "Abóbora", unit = "kg", price = 4.00),
                MarketProduct(name = "Chuchu", unit = "kg", price = 3.00),
                MarketProduct(name = "Berinjela", unit = "kg", price = 6.00),
                MarketProduct(name = "Pepino", unit = "kg", price = 4.50),
                MarketProduct(name = "Rabanete", unit = "kg", price = 5.00),
                MarketProduct(name = "Beterraba", unit = "kg", price = 4.00),
                MarketProduct(name = "Repolho", unit = "un", price = 3.50),
                MarketProduct(name = "Brócolis", unit = "kg", price = 8.00),
                MarketProduct(name = "Couve-flor", unit = "un", price = 6.00),
                MarketProduct(name = "Vagem", unit = "kg", price = 7.00),
                MarketProduct(name = "Ervilha", unit = "kg", price = 12.00),
                MarketProduct(name = "Milho", unit = "un", price = 2.50),
                MarketProduct(name = "Pimentão", unit = "kg", price = 8.00),
                MarketProduct(name = "Pepino", unit = "kg", price = 4.50),
                MarketProduct(name = "Abobrinha", unit = "kg", price = 5.00),
                MarketProduct(name = "Champignon", unit = "kg", price = 18.00),
                MarketProduct(name = "Uva", unit = "kg", price = 12.00),
                MarketProduct(name = "Pera", unit = "kg", price = 10.00),
                MarketProduct(name = "Pêssego", unit = "kg", price = 8.50),
                MarketProduct(name = "Ameixa", unit = "kg", price = 15.00),
                MarketProduct(name = "Mamão", unit = "un", price = 4.00),
                MarketProduct(name = "Melão", unit = "un", price = 6.00),
                MarketProduct(name = "Melancia", unit = "un", price = 8.00),
                MarketProduct(name = "Abacaxi", unit = "un", price = 5.00),
                MarketProduct(name = "Manga", unit = "kg", price = 6.50),
                MarketProduct(name = "Goiaba", unit = "kg", price = 5.00),
                MarketProduct(name = "Maracujá", unit = "kg", price = 8.00),
                MarketProduct(name = "Limão", unit = "kg", price = 4.00),
                MarketProduct(name = "Lima", unit = "kg", price = 4.50),
                MarketProduct(name = "Tangerina", unit = "kg", price = 5.50),
                MarketProduct(name = "Ponkan", unit = "kg", price = 6.00),
                MarketProduct(name = "Bergamota", unit = "kg", price = 5.50),
                MarketProduct(name = "Clementina", unit = "kg", price = 7.00),
                MarketProduct(name = "Tangerina", unit = "kg", price = 5.50),
                MarketProduct(name = "Ponkan", unit = "kg", price = 6.00),
                MarketProduct(name = "Bergamota", unit = "kg", price = 5.50),
                MarketProduct(name = "Clementina", unit = "kg", price = 7.00)
            )
            marketProductDao.insertAll(defaultProducts)
        }
    }
} 