package com.lourenc.trolly.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "item_lista",
    foreignKeys = [ForeignKey(
        entity = ListaCompra::class,
        parentColumns = ["id"],
        childColumns = ["idLista"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("idLista")]
)
data class ItemLista(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val idLista: Int,
    val nome: String,
    val quantidade: Int,
    val unidade: String,
    val precoUnitario: Double,
    val comprado: Boolean = false
)
