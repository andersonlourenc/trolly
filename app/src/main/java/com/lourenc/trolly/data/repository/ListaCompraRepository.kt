package com.lourenc.trolly.data.repository

import com.lourenc.trolly.data.local.dao.ListaCompraDao
import com.lourenc.trolly.data.local.entity.ListaCompra
import kotlinx.coroutines.flow.Flow


class ListaCompraRepository(private val listaDao: ListaCompraDao) {

    suspend fun insertLista(lista: ListaCompra) {
        listaDao.insert(lista)
    }

    fun getAllListas(): Flow<List<ListaCompra>> {
        return listaDao.getAll()
    }

    suspend fun deleteLista(lista: ListaCompra) {
        listaDao.delete(lista)
    }

    suspend fun updateLista(lista: ListaCompra) {
        listaDao.update(lista)
    }

    suspend fun getListaById(id: Int): ListaCompra? {
        return listaDao.getListaById(id)
    }
}
