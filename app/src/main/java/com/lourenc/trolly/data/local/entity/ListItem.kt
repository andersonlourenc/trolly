package com.lourenc.trolly.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "list_item",
    foreignKeys = [ForeignKey(
        entity = ShoppingList::class,
        parentColumns = ["id"],
        childColumns = ["shoppingListId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("shoppingListId")]
)
data class ListItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val shoppingListId: Int,
    val name: String,
    val quantity: Int,
    val unit: String,
    val unitPrice: Double,
    val purchased: Boolean = false
)
