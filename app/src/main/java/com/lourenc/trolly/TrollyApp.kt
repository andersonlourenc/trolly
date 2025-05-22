package com.lourenc.trolly

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.lourenc.trolly.data.local.db.AppDatabase
import com.lourenc.trolly.data.repository.ListaCompraRepository

class TrollyApp : Application() {
    lateinit var database: AppDatabase
        private set
        
    lateinit var repository: ListaCompraRepository
        private set

    override fun onCreate() {
        super.onCreate()
        
        try {
            // Inicializa o Firebase
            FirebaseApp.initializeApp(this)
            Log.d("TrollyApp", "Firebase inicializado com sucesso")
            
            // Inicializa o banco de dados
            database = AppDatabase.getInstance(this)
            repository = ListaCompraRepository(database.listaCompraDao())
            Log.d("TrollyApp", "Banco de dados e repositório inicializados com sucesso")
        } catch (e: Exception) {
            Log.e("TrollyApp", "Erro ao inicializar o app", e)
            throw e // Re-throw para garantir que o app não inicie em estado inconsistente
        }
    }
}
