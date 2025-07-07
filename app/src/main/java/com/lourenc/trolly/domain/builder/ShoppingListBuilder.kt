package com.lourenc.trolly.domain.builder

import com.lourenc.trolly.data.local.entity.ShoppingList
import java.util.Calendar

class ShoppingListBuilder {
    private var name: String = ""
    private var description: String = ""
    private var creationDate: Long = System.currentTimeMillis()
    private var estimatedTotal: Double = 0.0
    private var coverPhoto: String? = null
    private var status: String = "ACTIVE"

    fun setName(name: String): ShoppingListBuilder {
        this.name = name
        return this
    }

    fun setDescription(description: String): ShoppingListBuilder {
        this.description = description
        return this
    }

    fun setCreationDate(date: Long): ShoppingListBuilder {
        this.creationDate = date
        return this
    }

    fun setCreationDate(year: Int, month: Int, day: Int): ShoppingListBuilder {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        this.creationDate = calendar.timeInMillis
        return this
    }

    fun setEstimatedTotal(total: Double): ShoppingListBuilder {
        this.estimatedTotal = total
        return this
    }

    fun setCoverPhoto(photo: String?): ShoppingListBuilder {
        this.coverPhoto = photo
        return this
    }

    fun setStatus(status: String): ShoppingListBuilder {
        this.status = status
        return this
    }

    fun build(): ShoppingList {
        return ShoppingList(
            name = name,
            description = description,
            creationDate = creationDate,
            estimatedTotal = estimatedTotal,
            coverPhoto = coverPhoto,
            status = status
        )
    }
}
