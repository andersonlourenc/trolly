package com.lourenc.trolly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list")
data class ShoppingList(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    val creationDate: Long = System.currentTimeMillis(),
    val estimatedTotal: Double = 0.0,
    val coverPhoto: String? = null,
    val status: String = "ACTIVE" // ACTIVE or COMPLETED
)
