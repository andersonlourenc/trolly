package com.lourenc.trolly.domain.model

sealed class ListaCompraType {
    object Regular : ListaCompraType() {
        override val displayName: String = "Lista Regular"
        override val icon: String = "🛒"
        override val description: String = "Lista de compras comum"
    }
    
    object Recurrent : ListaCompraType() {
        override val displayName: String = "Lista Recorrente"
        override val icon: String = "🔄"
        override val description: String = "Lista que se repete periodicamente"
    }
    
    object Emergency : ListaCompraType() {
        override val displayName: String = "Lista de Emergência"
        override val icon: String = "🚨"
        override val description: String = "Lista para compras urgentes"
    }
    
    object Weekly : ListaCompraType() {
        override val displayName: String = "Lista Semanal"
        override val icon: String = "📅"
        override val description: String = "Lista para compras da semana"
    }
    
    object Monthly : ListaCompraType() {
        override val displayName: String = "Lista Mensal"
        override val icon: String = "📆"
        override val description: String = "Lista para compras do mês"
    }
    
    abstract val displayName: String
    abstract val icon: String
    abstract val description: String
} 