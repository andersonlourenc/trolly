package com.lourenc.trolly.domain.result

import com.lourenc.trolly.data.local.entity.ListaCompra

sealed class ListaCompraResult {
    data class Success(val data: Any) : ListaCompraResult()
    data class Error(val message: String, val exception: Exception? = null) : ListaCompraResult()
    object Loading : ListaCompraResult()
    
    // Resultados específicos
    data class ListaSuccess(val lista: ListaCompra) : ListaCompraResult()
    data class ListasSuccess(val listas: List<ListaCompra>) : ListaCompraResult()
    data class GastoMensalSuccess(val gasto: Double) : ListaCompraResult()
    data class ValorUltimaListaSuccess(val valor: Double) : ListaCompraResult()
} 