package com.lourenc.trolly.data.repository.interfaces

import com.lourenc.trolly.data.local.entity.ListaCompra
import kotlinx.coroutines.flow.Flow

interface ListaCompraRepository {
    fun getAllListas(): Flow<List<ListaCompra>>
    fun getListasAtivas(): Flow<List<ListaCompra>>
    fun getListasConcluidas(): Flow<List<ListaCompra>>
    suspend fun insertLista(lista: ListaCompra)
    suspend fun updateLista(lista: ListaCompra)
    suspend fun updateStatus(listaId: Int, status: String)
    suspend fun deleteLista(lista: ListaCompra)
    suspend fun getListaById(id: Int): ListaCompra?
    suspend fun getListasByMonth(month: Int, year: Int): List<ListaCompra>
    suspend fun getListasByDateRange(startDate: Long, endDate: Long): List<ListaCompra>
} 