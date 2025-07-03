package com.lourenc.trolly.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ShoppingListFormatter {
    
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
        

        fun formatValue(value: Double): String {
            return currencyFormat.format(value)
        }
        

        fun formatValueWithDash(value: Double): String {
            return if (value > 0.0) {
                currencyFormat.format(value)
            } else {
                "—"
            }
        }
        

        fun formatValueWithoutSymbol(value: Double): String {
            return String.format(Locale("pt", "BR"), "%.2f", value)
        }
        

        fun getMonthInPortuguese(month: Int): String {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MONTH, month)
            return monthFormat.format(calendar.time).replaceFirstChar { it.uppercase() }
        }
        

        fun getCurrentMonthInPortuguese(): String {
            return getMonthInPortuguese(Calendar.getInstance().get(Calendar.MONTH))
        }
        

        fun formatQuantity(quantity: Int, unit: String): String {
            return "$quantity $unit"
        }
        

        fun formatDescription(description: String, maxLength: Int = 50): String {
            return if (description.length <= maxLength) {
                description
            } else {
                description.substring(0, maxLength - 3) + "..."
            }
        }
        

        fun formatListName(name: String, maxLength: Int = 30): String {
            return if (name.length <= maxLength) {
                name
            } else {
                name.substring(0, maxLength - 3) + "..."
            }
        }
        

        fun calculateElapsedTime(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp
            
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            
            return when {
                days > 0 -> "$days dia${if (days > 1) "s" else ""}"
                hours > 0 -> "$hours hora${if (hours > 1) "s" else ""}"
                minutes > 0 -> "$minutes minuto${if (minutes > 1) "s" else ""}"
                else -> "Agora"
            }
        }
    }
} 