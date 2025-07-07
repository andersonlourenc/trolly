package com.lourenc.trolly.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lourenc.trolly.data.local.dao.ListItemDao
import com.lourenc.trolly.data.local.dao.MarketProductDao
import com.lourenc.trolly.data.local.dao.ShoppingListDao
import com.lourenc.trolly.data.local.entity.ListItem
import com.lourenc.trolly.data.local.entity.MarketProduct
import com.lourenc.trolly.data.local.entity.ShoppingList

@Database(
    entities = [ShoppingList::class, ListItem::class, MarketProduct::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun listItemDao(): ListItemDao
    abstract fun marketProductDao(): MarketProductDao

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
                        android.util.Log.d("AppDatabase", "Database created successfully")
                    }
                    
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        android.util.Log.d("AppDatabase", "Database opened successfully")
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
