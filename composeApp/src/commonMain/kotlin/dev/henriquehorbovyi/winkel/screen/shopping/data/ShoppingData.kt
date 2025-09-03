package dev.henriquehorbovyi.winkel.screen.shopping.data

import dev.henriquehorbovyi.winkel.data.local.item.ShoppingItemEntity
import org.jetbrains.compose.resources.StringResource

data class ShoppingItem(
    val id: Long? = null,
    val name: String,
    val quantity: Int,
    val price: Double,
    val isBought: Boolean,
    val shoppingListId: Long? = null,
    val isTemporary: Boolean = false,
)

fun ShoppingItemEntity.toItem() = ShoppingItem(
    id = id,
    name = name,
    quantity = quantity,
    price = price,
    isBought = isBought,
    shoppingListId = shoppingListId
)

fun ShoppingItem.toEntity(): ShoppingItemEntity {
    if (shoppingListId == null) throw IllegalArgumentException("ShoppingListId cannot be null")
    return ShoppingItemEntity(
        id = id,
        name = name,
        quantity = quantity,
        price = price,
        isBought = isBought,
        shoppingListId = shoppingListId
    )
}


sealed interface ShoppingAction {
    data class SaveItem(val item: ShoppingItem) : ShoppingAction
    data class RemoveItem(val item: ShoppingItem) : ShoppingAction
    data class MarkAsBought(val item: ShoppingItem, val isBought: Boolean) : ShoppingAction
    object AddItem : ShoppingAction
    object CancelEditing : ShoppingAction
}

sealed interface ShoppingState {
    data class Content(val data: ShoppingData) : ShoppingState
    data class Error(val messageResId: StringResource) : ShoppingState
    object Loading : ShoppingState
}

data class ShoppingData(
    val items: List<ShoppingItem>,
    val totalPrice: Double,
    val shoppingListName: String,
)

sealed interface ShoppingNavigationEvent {
    object ClearForm : ShoppingNavigationEvent
    data class ErrorMessage(val messageRes: StringResource) : ShoppingNavigationEvent
    object NavigateBack : ShoppingNavigationEvent
}