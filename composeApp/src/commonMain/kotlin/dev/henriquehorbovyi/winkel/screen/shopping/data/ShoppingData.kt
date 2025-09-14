package dev.henriquehorbovyi.winkel.screen.shopping.data

import dev.henriquehorbovyi.winkel.core.MoneyConverter
import dev.henriquehorbovyi.winkel.data.local.item.ShoppingItemEntity
import org.jetbrains.compose.resources.StringResource

data class ShoppingItem(
    val id: Long? = null,
    val name: String,
    val quantity: Int = 1,
    val price: Double,
    val maskedPrice: String, // for the UI representation (R$ 2,0)
    val isBought: Boolean,
    val shoppingListId: Long? = null,
    val isEditing: Boolean = false,
)

fun ShoppingItemEntity.toItem(converter: MoneyConverter) = ShoppingItem(
    id = id,
    name = name,
    quantity = quantity,
    maskedPrice = converter.formatAsCurrency(price),
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
    data class OnShoppingItemChanged(val item: ShoppingItem) : ShoppingAction
    data class EditItem(val itemIndex: Int) : ShoppingAction
    data class OnEditingItemChanged(val index: Int, val item: ShoppingItem) : ShoppingAction
    data class UpdateItem(val item: ShoppingItem) : ShoppingAction
    data class SaveItem(val item: ShoppingItem) : ShoppingAction
    data class ConfirmRemoveItem(val item: ShoppingItem) : ShoppingAction
    data class RemoveItem(val item: ShoppingItem) : ShoppingAction
    data class MarkAsBought(val item: ShoppingItem, val isBought: Boolean) : ShoppingAction
    data class CancelEditing(val itemIndex: Int) : ShoppingAction
    data class OnEditShoppingListName(val name: String) : ShoppingAction
    data class SaveShoppingListName(val name: String) : ShoppingAction
    object StartEditingShoppingListName : ShoppingAction
}

sealed interface ShoppingState {
    data class Content(val data: ShoppingData) : ShoppingState
    data class Error(val messageResId: StringResource) : ShoppingState
    object Loading : ShoppingState
}

data class ShoppingData(
    val items: List<ShoppingItem>,
    val totalPrice: String,
    val shoppingListName: String,
    val editingShoppingItem: ShoppingItem? = null,
    val isEditingShoppingListName: Boolean = false,
    val currentNewShoppingItem: ShoppingItem = ShoppingItem(
        name = "",
        maskedPrice = "",
        isBought = false,
        price = 0.0
    )
)

sealed interface ShoppingNavigationEvent {
    data class ErrorMessage(val messageRes: StringResource) : ShoppingNavigationEvent
    data class ConfirmRemoveItem(val item: ShoppingItem) : ShoppingNavigationEvent
    object NavigateBack : ShoppingNavigationEvent
}