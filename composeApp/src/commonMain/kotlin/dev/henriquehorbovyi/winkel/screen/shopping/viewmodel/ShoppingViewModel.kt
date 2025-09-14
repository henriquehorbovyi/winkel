package dev.henriquehorbovyi.winkel.screen.shopping.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dev.henriquehorbovyi.winkel.core.MoneyConverter
import dev.henriquehorbovyi.winkel.data.local.shoppings.ShoppingListEntity
import dev.henriquehorbovyi.winkel.data.repository.IShoppingRepository
import dev.henriquehorbovyi.winkel.navigation.MainGraph
import dev.henriquehorbovyi.winkel.screen.shopping.data.ShoppingAction
import dev.henriquehorbovyi.winkel.screen.shopping.data.ShoppingData
import dev.henriquehorbovyi.winkel.screen.shopping.data.ShoppingItem
import dev.henriquehorbovyi.winkel.screen.shopping.data.ShoppingNavigationEvent
import dev.henriquehorbovyi.winkel.screen.shopping.data.ShoppingState
import dev.henriquehorbovyi.winkel.screen.shopping.data.toEntity
import dev.henriquehorbovyi.winkel.screen.shopping.data.toItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import winkel.composeapp.generated.resources.Res
import winkel.composeapp.generated.resources.shopping_list_item_name_cant_be_empty_error
import winkel.composeapp.generated.resources.shopping_list_item_price_cant_be_negative
import winkel.composeapp.generated.resources.shopping_list_item_price_cant_be_zero

interface IShoppingViewModel {
    val uiState: StateFlow<ShoppingState>

    val navigationEvent: Channel<ShoppingNavigationEvent>

    fun onAction(action: ShoppingAction)
}

class ShoppingViewModel(
    savedStateHandle: SavedStateHandle,
    private val shoppingRepository: IShoppingRepository,
    private val moneyConverter: MoneyConverter
) : IShoppingViewModel, ViewModel() {
    override val uiState = MutableStateFlow<ShoppingState>(ShoppingState.Loading)

    override val navigationEvent = Channel<ShoppingNavigationEvent>()

    private var shoppingId: Long? = savedStateHandle.toRoute<MainGraph.Shopping>().shoppingListId

    init {
        viewModelScope.launch {
            val shopping = if (shoppingId == null) {
                shoppingRepository.saveShopping(ShoppingListEntity(name = "My awesome list"))
                shoppingRepository.lastInsertedShopping().also { shoppingId = it.id }
            } else {
                shoppingRepository.getById(shoppingId ?: 0)
            }
            shoppingRepository
                .getAllItemsWithTotalBoughtPrice(shopping.id)
                .collect { items ->
                    uiState.update {
                        ShoppingState.Content(
                            data = ShoppingData(
                                shoppingListName = shopping.name,
                                totalPrice = moneyConverter.formatAsCurrency(items.totalPrice),
                                items = items.items.map { it.toItem(moneyConverter) }
                            )
                        )
                    }
                }
        }
    }

    override fun onAction(action: ShoppingAction) {
        when (action) {
            is ShoppingAction.SaveItem -> handleSaveItem(action.item)
            is ShoppingAction.RemoveItem -> handleRemoveItem(action.item)
            is ShoppingAction.MarkAsBought -> handleMarkAsBought(action.item, action.isBought)
            is ShoppingAction.UpdateItem -> updateShoppingItem(action.item)
            is ShoppingAction.ConfirmRemoveItem -> confirmRemoveItem(action.item)
            is ShoppingAction.OnShoppingItemChanged -> onShoppingItemChanged(action.item)
            is ShoppingAction.EditItem -> editItem(action.itemIndex)
            is ShoppingAction.CancelEditing -> handleCancelEditing(action.itemIndex)
            is ShoppingAction.OnEditingItemChanged -> onEditingItemChanged(item = action.item)
            is ShoppingAction.OnEditShoppingListName -> onEditShoppingListName(action.name)
            ShoppingAction.StartEditingShoppingListName -> startEditingShoppingListName()
            is ShoppingAction.SaveShoppingListName -> saveShoppingListName(action.name)
        }
    }

    private fun saveShoppingListName(name: String) {
        val id = shoppingId
        if (id == null) return
        viewModelScope.launch {
            shoppingRepository.updateShopping(id, name)
            uiState.update {
                val state = (uiState.value as ShoppingState.Content)
                state.copy(data = state.data.copy(isEditingShoppingListName = false))
            }
        }
    }

    private fun startEditingShoppingListName() {
        uiState.update {
            val state = (uiState.value as ShoppingState.Content)
            state.copy(data = state.data.copy(isEditingShoppingListName = true))
        }
    }

    private fun onEditShoppingListName(name: String) {
        viewModelScope.launch {
            val state = (uiState.value as ShoppingState.Content)
            uiState.update { state.copy(data = state.data.copy(shoppingListName = name)) }
        }
    }

    private fun onShoppingItemChanged(item: ShoppingItem) {
        if (item.quantity < 1) return
        viewModelScope.launch {
            val state = (uiState.value as ShoppingState.Content)
            val price = moneyConverter.convertToDecimal(item.maskedPrice)
            uiState.update {
                state.copy(data = state.data.copy(currentNewShoppingItem = item.copy(price = price)))
            }
        }
    }

    private fun onEditingItemChanged(item: ShoppingItem) {
        if (item.quantity < 1) return
        viewModelScope.launch {
            val state = (uiState.value as ShoppingState.Content)
            val price = moneyConverter.convertToDecimal(item.maskedPrice)
            uiState.update {
                state.copy(
                    data = state.data.copy(
                        editingShoppingItem = item.copy(
                            price = price
                        )
                    )
                )
            }
        }
    }

    private fun editItem(itemIndex: Int) {
        viewModelScope.launch {
            val state = (uiState.value as ShoppingState.Content)
            val item = state.data.items[itemIndex]
            val updatedItem = item.copy(
                isEditing = true,
                maskedPrice = moneyConverter.decimalToCents(item.price).toString()
            )
            uiState.update {
                state.copy(
                    data = state.data.copy(
                        editingShoppingItem = updatedItem,
                        items = state.data.items
                            .toMutableList()
                            .apply { set(itemIndex, item.copy(isEditing = true)) }
                    )
                )
            }
        }
    }

    private fun handleCancelEditing(itemIndex: Int) {
        viewModelScope.launch {
            val state = (uiState.value as ShoppingState.Content)
            val updatedItem = state.data.items[itemIndex].copy(isEditing = false)
            val updatedList =
                state.data.items.toMutableList().apply { set(itemIndex, updatedItem) }
            uiState.update {
                state.copy(
                    data = state.data.copy(
                        items = updatedList,
                        editingShoppingItem = null
                    )
                )
            }
        }
    }

    private fun handleSaveItem(item: ShoppingItem) {
        viewModelScope.launch {
            if (!validateItem(item)) {
                return@launch
            }

            val shoppingId = item.shoppingListId ?: shoppingId
            if (shoppingId != null) {
                shoppingRepository.saveShoppingItem(
                    item.copy(shoppingListId = shoppingId).toEntity()
                )
            }
        }
    }

    private fun confirmRemoveItem(item: ShoppingItem) {
        viewModelScope.launch {
            navigationEvent.send(ShoppingNavigationEvent.ConfirmRemoveItem(item))
        }
    }

    private fun handleRemoveItem(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.deleteShoppingItem(item.toEntity())
        }
    }

    private fun handleMarkAsBought(item: ShoppingItem, isBought: Boolean) {
        val updatedItem = item.copy(isBought = isBought)
        updateShoppingItem(updatedItem)
    }

    private fun updateShoppingItem(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.updateShoppingItem(item.copy(isEditing = false).toEntity())
        }
    }

    private suspend fun validateItem(item: ShoppingItem): Boolean {
        if (item.name.isEmpty()) {
            navigationEvent.send(ShoppingNavigationEvent.ErrorMessage(Res.string.shopping_list_item_name_cant_be_empty_error))
            return false
        }
        if (item.maskedPrice.isEmpty()) {
            navigationEvent.send(ShoppingNavigationEvent.ErrorMessage(Res.string.shopping_list_item_price_cant_be_zero))
            return false
        }
        if (moneyConverter.convertToDecimal(item.maskedPrice) < 0) {
            navigationEvent.send(ShoppingNavigationEvent.ErrorMessage(Res.string.shopping_list_item_price_cant_be_negative))
            return false
        }
        return true
    }
}
