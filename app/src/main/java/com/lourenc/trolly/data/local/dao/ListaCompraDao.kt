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

    @Query("SELECT * FROM lista_compra WHERE status = 'ATIVA' ORDER BY dataCriacao DESC")
    fun getListasAtivas(): kotlinx.coroutines.flow.Flow<List<ListaCompra>>

    @Query("SELECT * FROM lista_compra WHERE status = 'CONCLUIDA' ORDER BY dataCriacao DESC")
    fun getListasConcluidas(): kotlinx.coroutines.flow.Flow<List<ListaCompra>>

    @Query("SELECT * FROM lista_compra WHERE id = :id")
    suspend fun getListaById(id: Int): ListaCompra?

    @Query("SELECT * FROM lista_compra WHERE dataCriacao BETWEEN :startDate AND :endDate ORDER BY dataCriacao DESC")
    suspend fun getListasByDateRange(startDate: Long, endDate: Long): List<ListaCompra>

    @Query("UPDATE lista_compra SET status = :status WHERE id = :listaId")
    suspend fun updateStatus(listaId: Int, status: String)
}
