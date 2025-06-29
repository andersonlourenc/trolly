package com.lourenc.trolly.domain.usecase

import com.lourenc.trolly.data.local.entity.ListaCompra
import com.lourenc.trolly.domain.model.ListaCompraType
import com.lourenc.trolly.domain.result.ListaCompraResult

interface ListaCompraUseCase {
    suspend fun createLista(lista: ListaCompra): ListaCompraResult
    suspend fun createListaWithType(type: ListaCompraType, nome: String, descricao: String = ""): ListaCompraResult
    suspend fun updateLista(lista: ListaCompra): ListaCompraResult
    suspend fun updateStatus(listaId: Int, status: String): ListaCompraResult
    suspend fun deleteLista(lista: ListaCompra): ListaCompraResult
    suspend fun getListaById(id: Int): ListaCompraResult
    suspend fun getAllListas(): ListaCompraResult
    suspend fun getListasAtivas(): ListaCompraResult
    suspend fun getListasConcluidas(): ListaCompraResult
    suspend fun getListasByMonth(month: Int, year: Int): ListaCompraResult
    suspend fun getListasByDateRange(startDate: Long, endDate: Long): ListaCompraResult
    suspend fun calcularGastoMensal(month: Int, year: Int): ListaCompraResult
    suspend fun calcularValorUltimaLista(): ListaCompraResult
    suspend fun getListasByType(type: ListaCompraType): ListaCompraResult
} 