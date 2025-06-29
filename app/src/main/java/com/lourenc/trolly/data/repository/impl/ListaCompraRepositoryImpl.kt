package com.lourenc.trolly.data.repository.impl

import com.lourenc.trolly.data.local.dao.ListaCompraDao
import com.lourenc.trolly.data.local.entity.ListaCompra
import com.lourenc.trolly.data.repository.interfaces.ListaCompraRepository
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class ListaCompraRepositoryImpl(private val listaDao: ListaCompraDao) : ListaCompraRepository {

    override suspend fun insertLista(lista: ListaCompra) {
        try {
            println("DEBUG: Repository - Iniciando inserção da lista: ${lista.name}")
            listaDao.insert(lista)
            println("DEBUG: Repository - Lista inserida no DAO com sucesso")
        } catch (e: Exception) {
            println("DEBUG: Repository - Erro ao inserir lista: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    override fun getAllListas(): Flow<List<ListaCompra>> {
        return listaDao.getAll()
    }

    override fun getListasAtivas(): Flow<List<ListaCompra>> {
        return listaDao.getListasAtivas()
    }

    override fun getListasConcluidas(): Flow<List<ListaCompra>> {
        return listaDao.getListasConcluidas()
    }

    override suspend fun deleteLista(lista: ListaCompra) {
        listaDao.delete(lista)
    }

    override suspend fun updateLista(lista: ListaCompra) {
        listaDao.update(lista)
    }

    override suspend fun updateStatus(listaId: Int, status: String) {
        listaDao.updateStatus(listaId, status)
    }

    override suspend fun getListaById(id: Int): ListaCompra? {
        return listaDao.getListaById(id)
    }

    override suspend fun getListasByMonth(month: Int, year: Int): List<ListaCompra> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis
        
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val endDate = calendar.timeInMillis
        
        return listaDao.getListasByDateRange(startDate, endDate)
    }

    override suspend fun getListasByDateRange(startDate: Long, endDate: Long): List<ListaCompra> {
        return listaDao.getListasByDateRange(startDate, endDate)
    }
} 