package com.lourenc.trolly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lista_compra")
data class ListaCompra(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val descricao: String,
    val dataCriacao: Long = System.currentTimeMillis(),
    val totalEstimado: Double,
    val fotoCapa: String? = null
)
