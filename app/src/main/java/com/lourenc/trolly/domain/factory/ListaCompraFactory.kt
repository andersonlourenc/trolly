package com.lourenc.trolly.domain.factory

import com.lourenc.trolly.data.local.entity.ListaCompra
import com.lourenc.trolly.domain.model.ListaCompraType
import java.util.Calendar

interface ListaCompraFactory {
    fun createLista(type: ListaCompraType, nome: String, descricao: String = ""): ListaCompra
    fun createListaWithDefaultName(type: ListaCompraType): ListaCompra
}

class ListaCompraFactoryImpl : ListaCompraFactory {
    
    override fun createLista(type: ListaCompraType, nome: String, descricao: String): ListaCompra {
        val currentTime = System.currentTimeMillis()
        
        return when (type) {
            is ListaCompraType.Regular -> {
                ListaCompra(
                    name = nome,
                    descricao = descricao,
                    dataCriacao = currentTime,
                    totalEstimado = 0.0
                )
            }
            
            is ListaCompraType.Recurrent -> {
                ListaCompra(
                    name = nome,
                    descricao = descricao,
                    dataCriacao = currentTime,
                    totalEstimado = 0.0
                )
            }
            
            is ListaCompraType.Emergency -> {
                ListaCompra(
                    name = nome,
                    descricao = descricao,
                    dataCriacao = currentTime,
                    totalEstimado = 0.0
                )
            }
            
            is ListaCompraType.Weekly -> {
                val calendar = Calendar.getInstance()
                val weekNumber = calendar.get(Calendar.WEEK_OF_YEAR)
                val year = calendar.get(Calendar.YEAR)
                
                ListaCompra(
                    name = nome.ifBlank { "Lista Semanal ${weekNumber}/${year}" },
                    descricao = descricao.ifBlank { "Lista de compras da semana ${weekNumber}" },
                    dataCriacao = currentTime,
                    totalEstimado = 0.0
                )
            }
            
            is ListaCompraType.Monthly -> {
                val calendar = Calendar.getInstance()
                val month = calendar.get(Calendar.MONTH)
                val year = calendar.get(Calendar.YEAR)
                val monthName = getMonthName(month)
                
                ListaCompra(
                    name = nome.ifBlank { "Lista Mensal ${monthName}/${year}" },
                    descricao = descricao.ifBlank { "Lista de compras de ${monthName}" },
                    dataCriacao = currentTime,
                    totalEstimado = 0.0
                )
            }
        }
    }
    
    override fun createListaWithDefaultName(type: ListaCompraType): ListaCompra {
        return createLista(type, "", "")
    }
    
    private fun getMonthName(month: Int): String {
        return when (month) {
            Calendar.JANUARY -> "Janeiro"
            Calendar.FEBRUARY -> "Fevereiro"
            Calendar.MARCH -> "Março"
            Calendar.APRIL -> "Abril"
            Calendar.MAY -> "Maio"
            Calendar.JUNE -> "Junho"
            Calendar.JULY -> "Julho"
            Calendar.AUGUST -> "Agosto"
            Calendar.SEPTEMBER -> "Setembro"
            Calendar.OCTOBER -> "Outubro"
            Calendar.NOVEMBER -> "Novembro"
            Calendar.DECEMBER -> "Dezembro"
            else -> "Desconhecido"
        }
    }
} 