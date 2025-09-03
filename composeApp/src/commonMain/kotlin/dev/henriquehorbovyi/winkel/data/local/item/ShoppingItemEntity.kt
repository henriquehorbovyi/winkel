package dev.henriquehorbovyi.winkel.data.local.item

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val name: String,
    val quantity: Int,
    val price: Double,
    val isBought: Boolean,
    val shoppingListId: Long
)

data class ShoppingItemWithTotalPrice(
    val items: List<ShoppingItemEntity>,
    val totalPrice: Double
)