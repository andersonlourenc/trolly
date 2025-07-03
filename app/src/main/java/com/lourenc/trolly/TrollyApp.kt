package com.lourenc.trolly

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.lourenc.trolly.data.local.db.AppDatabase
import com.lourenc.trolly.data.repository.ListItemRepositoryImpl
import com.lourenc.trolly.data.repository.ShoppingListRepositoryImpl
import com.lourenc.trolly.domain.repository.ListItemRepository
import com.lourenc.trolly.domain.repository.ShoppingListRepository
import com.lourenc.trolly.domain.usecase.ListItemUseCase
import com.lourenc.trolly.domain.usecase.ShoppingListUseCase
import com.lourenc.trolly.domain.usecase.ListItemUseCaseImpl
import com.lourenc.trolly.domain.usecase.ShoppingListUseCaseImpl
import com.lourenc.trolly.di.AppModule

class TrollyApp : Application() {
    lateinit var database: AppDatabase
        private set
        
    lateinit var shoppingListRepository: ShoppingListRepository
        private set
        
    lateinit var listItemRepository: ListItemRepository
        private set
        
    lateinit var shoppingListUseCase: ShoppingListUseCase
        private set
        
    lateinit var listItemUseCase: ListItemUseCase
        private set

    override fun onCreate() {
        super.onCreate()
        
        try {
            // Inicializa o Firebase
            FirebaseApp.initializeApp(this)
            Log.d("TrollyApp", "Firebase initialized successfully")
            
            // Verificar se o Firebase Auth está funcionando
            val auth = FirebaseAuth.getInstance()
            Log.d("TrollyApp", "Firebase Auth initialized: ${auth.app.name}")
            
            // Inicializa o banco de dados
            Log.d("TrollyApp", "Starting database creation...")
            database = AppDatabase.getInstance(this)
            Log.d("TrollyApp", "Database created successfully")
            
            // Inicializa repositórios usando AppModule
            Log.d("TrollyApp", "Starting repository creation...")
            shoppingListRepository = AppModule.provideShoppingListRepository(database.shoppingListDao())
            listItemRepository = AppModule.provideListItemRepository(database.listItemDao())
            Log.d("TrollyApp", "Repositories created successfully")
            
            // Inicializa use cases usando AppModule
            Log.d("TrollyApp", "Starting use case creation...")
            shoppingListUseCase = AppModule.provideShoppingListUseCase(shoppingListRepository, listItemRepository)
            listItemUseCase = AppModule.provideListItemUseCase(listItemRepository)
            Log.d("TrollyApp", "Use cases created successfully")
            
            Log.d("TrollyApp", "Database, repositories and use cases initialized successfully")
        } catch (e: Exception) {
            Log.e("TrollyApp", "Error initializing app", e)
            e.printStackTrace()
            throw e // Re-throw para garantir que o app não inicie em estado inconsistente
        }
    }
}
