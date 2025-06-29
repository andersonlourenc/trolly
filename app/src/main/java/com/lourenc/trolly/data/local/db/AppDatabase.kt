package com.lourenc.trolly.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lourenc.trolly.data.local.dao.ItemListaDao
import com.lourenc.trolly.data.local.dao.ListaCompraDao
import com.lourenc.trolly.data.local.entity.ItemLista
import com.lourenc.trolly.data.local.entity.ListaCompra

@Database(
    entities = [ListaCompra::class, ItemLista::class],
    version = 2,
    exportSchema = false
)
abstract class  AppDatabase : RoomDatabase() {
    abstract fun listaCompraDao(): ListaCompraDao
    abstract fun itemListaDao(): ItemListaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trolly_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        android.util.Log.d("AppDatabase", "Banco de dados criado com sucesso")
                    }
                    
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        android.util.Log.d("AppDatabase", "Banco de dados aberto com sucesso")
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
