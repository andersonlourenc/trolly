package com.lourenc.trolly

import android.app.Application
import android.content.Context
import androidx.room.Room

import com.lourenc.trolly.data.local.db.AppDatabase
import com.lourenc.trolly.data.repository.ListaCompraRepository

class TrollyApp : Application() {


    val database: AppDatabase by lazy { getDatabase(this) }

    val repository: ListaCompraRepository by lazy {
        ListaCompraRepository(database.listaCompraDao())
    }

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_database_name"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
