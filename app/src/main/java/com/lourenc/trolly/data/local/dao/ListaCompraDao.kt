package com.lourenc.trolly.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lourenc.trolly.data.local.entity.ListaCompra

@Dao
interface ListaCompraDao {
    @Insert
    suspend fun insert(lista: ListaCompra)

    @Update
    suspend fun update(lista: ListaCompra)

    @Delete
    suspend fun delete(lista: ListaCompra)

    @Query("SELECT * FROM lista_compra ORDER BY dataCriacao DESC")
    fun getAll(): kotlinx.coroutines.flow.Flow<List<ListaCompra>>


    @Query("SELECT * FROM lista_compra WHERE id = :id")
    suspend fun getId(id: Int): ListaCompra?
}
