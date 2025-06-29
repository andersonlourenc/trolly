package com.lourenc.trolly

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.lourenc.trolly.data.local.db.AppDatabase
import com.lourenc.trolly.data.repository.impl.ItemListaRepositoryImpl
import com.lourenc.trolly.data.repository.impl.ListaCompraRepositoryImpl
import com.lourenc.trolly.data.repository.interfaces.ItemListaRepository
import com.lourenc.trolly.data.repository.interfaces.ListaCompraRepository
import com.lourenc.trolly.domain.usecase.ItemListaUseCase
import com.lourenc.trolly.domain.usecase.ListaCompraUseCase
import com.lourenc.trolly.domain.usecase.impl.ItemListaUseCaseImpl
import com.lourenc.trolly.domain.usecase.impl.ListaCompraUseCaseImpl
import com.lourenc.trolly.di.AppModule

class TrollyApp : Application() {
    lateinit var database: AppDatabase
        private set
        
    lateinit var listaRepository: ListaCompraRepository
        private set
        
    lateinit var itemRepository: ItemListaRepository
        private set
        
    lateinit var listaUseCase: ListaCompraUseCase
        private set
        
    lateinit var itemUseCase: ItemListaUseCase
        private set

    override fun onCreate() {
        super.onCreate()
        
        try {
            // Inicializa o Firebase
            FirebaseApp.initializeApp(this)
            Log.d("TrollyApp", "Firebase inicializado com sucesso")
            
            // Verificar se o Firebase Auth está funcionando
            val auth = FirebaseAuth.getInstance()
            Log.d("TrollyApp", "Firebase Auth inicializado: ${auth.app.name}")
            
            // Inicializa o banco de dados
            Log.d("TrollyApp", "Iniciando criação do banco de dados...")
            database = AppDatabase.getInstance(this)
            Log.d("TrollyApp", "Banco de dados criado com sucesso")
            
            // Inicializa repositórios usando AppModule
            Log.d("TrollyApp", "Iniciando criação dos repositórios...")
            listaRepository = AppModule.provideListaCompraRepository(database.listaCompraDao())
            itemRepository = AppModule.provideItemListaRepository(database.itemListaDao())
            Log.d("TrollyApp", "Repositórios criados com sucesso")
            
            // Inicializa use cases usando AppModule
            Log.d("TrollyApp", "Iniciando criação dos use cases...")
            listaUseCase = AppModule.provideListaCompraUseCase(listaRepository, itemRepository)
            itemUseCase = AppModule.provideItemListaUseCase(itemRepository)
            Log.d("TrollyApp", "Use cases criados com sucesso")
            
            Log.d("TrollyApp", "Banco de dados, repositórios e use cases inicializados com sucesso")
        } catch (e: Exception) {
            Log.e("TrollyApp", "Erro ao inicializar o app", e)
            e.printStackTrace()
            throw e // Re-throw para garantir que o app não inicie em estado inconsistente
        }
    }
}
