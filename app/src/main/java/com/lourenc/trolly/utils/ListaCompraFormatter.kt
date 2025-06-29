package com.lourenc.trolly.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ListaCompraFormatter {
    
    companion object {
        private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
        private val monthFormat = SimpleDateFormat("MMMM", Locale("pt", "BR"))
        private val currencyFormat = java.text.NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        

        fun formatDate(timestamp: Long): String {
            return dateFormat.format(Date(timestamp))
        }
        

        fun formatDateTime(timestamp: Long): String {
            return dateTimeFormat.format(Date(timestamp))
        }
        

        fun formatarValor(valor: Double): String {
            return currencyFormat.format(valor)
        }
        

        fun formatarValorSemSimbolo(valor: Double): String {
            return String.format(Locale("pt", "BR"), "%.2f", valor)
        }
        

        fun getMesEmPortugues(month: Int): String {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MONTH, month)
            return monthFormat.format(calendar.time).replaceFirstChar { it.uppercase() }
        }
        

        fun getMesAtualEmPortugues(): String {
            return getMesEmPortugues(Calendar.getInstance().get(Calendar.MONTH))
        }
        

        fun formatarQuantidade(quantidade: Int, unidade: String): String {
            return "$quantidade $unidade"
        }
        

        fun formatarDescricao(descricao: String, maxLength: Int = 50): String {
            return if (descricao.length <= maxLength) {
                descricao
            } else {
                descricao.substring(0, maxLength - 3) + "..."
            }
        }
        

        fun formatarNomeLista(nome: String, maxLength: Int = 30): String {
            return if (nome.length <= maxLength) {
                nome
            } else {
                nome.substring(0, maxLength - 3) + "..."
            }
        }
        

        fun calcularTempoDecorrido(timestamp: Long): String {
            val agora = System.currentTimeMillis()
            val diferenca = agora - timestamp
            
            val segundos = diferenca / 1000
            val minutos = segundos / 60
            val horas = minutos / 60
            val dias = horas / 24
            
            return when {
                dias > 0 -> "$dias dia${if (dias > 1) "s" else ""}"
                horas > 0 -> "$horas hora${if (horas > 1) "s" else ""}"
                minutos > 0 -> "$minutos minuto${if (minutos > 1) "s" else ""}"
                else -> "Agora"
            }
        }
    }
} 