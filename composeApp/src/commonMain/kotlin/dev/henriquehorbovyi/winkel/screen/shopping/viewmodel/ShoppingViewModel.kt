package dev.henriquehorbovyi.winkel.screen.shopping.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import winkel.composeapp.generated.resources.Res
import winkel.composeapp.generated.resources.shopping_list_item_name_cant_be_empty_error
import winkel.composeapp.generated.resources.shopping_list_item_price_cant_be_negative
import winkel.composeapp.generated.resources.shopping_list_item_price_cant_be_zero

interface IShoppingViewModel {
    val uiState: StateFlow<ShoppingState>

    val navigationEvent: Flow<ShoppingNavigationEvent>

    fun onAction(action: ShoppingAction)
}

class ShoppingViewModel(
    savedStateHandle: SavedStateHandle,
    private val shoppingRepository: IShoppingRepository,
) : IShoppingViewModel, ViewModel() {
    override val uiState = MutableStateFlow<ShoppingState>(ShoppingState.Loading)

    private val _navigationEvent = Channel<ShoppingNavigationEvent>()
    override val navigationEvent: Flow<ShoppingNavigationEvent> = _navigationEvent.receiveAsFlow()

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
                                totalPrice = items.totalPrice,
                                items = items.items.map { it.toItem() }
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
            ShoppingAction.AddItem -> handleAddItem()
            ShoppingAction.CancelEditing -> handleCancelEditing()
        }
    }

    private fun handleAddItem() {
        viewModelScope.launch {
            val state = (uiState.value as ShoppingState.Content)
            if (shoppingId == null) {
                TODO("Shopping ID is null, I need to handle this case with an error message")
            }

            val data = state.data
            val updatedItems = data.items + ShoppingItem(
                isTemporary = true,
                name = "",
                quantity = 1,
                price = 0.0,
                isBought = false,
                shoppingListId = shoppingId ?: 0
            )
            val updatedData = data.copy(items = updatedItems)
            uiState.update { state.copy(data = updatedData) }
        }
    }

    private fun handleCancelEditing() {
        viewModelScope.launch {
            val state = (uiState.value as ShoppingState.Content)
            val temporaryItem = state.data.items.firstOrNull()
            if (temporaryItem == null) {
                return@launch
            }
            val updatedData = state.data.copy(items = state.data.items - temporaryItem)
            uiState.update {
                state.copy(data = updatedData)
            }
        }
    }

    private fun handleSaveItem(item: ShoppingItem) {
        viewModelScope.launch {
            if (!validateItem(item)) {
                return@launch
            }
            /*
            * TODO: Form validation
            *  - name can't be empty
            *  - price can't be zero
            *  - quantity should not be less than 1
            * */
            val shoppingId = item.shoppingListId ?: shoppingId
            if (shoppingId != null) {
                shoppingRepository.saveShoppingItem(
                    item.copy(shoppingListId = shoppingId).toEntity()
                )
                _navigationEvent.send(ShoppingNavigationEvent.ClearForm)
            }
        }
    }

    private fun handleRemoveItem(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.deleteShoppingItem(item.toEntity())
        }
    }

    private fun handleMarkAsBought(item: ShoppingItem, isBought: Boolean) {
        viewModelScope.launch {
            val updatedItem = item.copy(isBought = isBought).toEntity()
            shoppingRepository.updateShoppingItem(updatedItem)
        }
    }

    private suspend fun validateItem(item: ShoppingItem): Boolean {
        if (item.name.isEmpty()) {
            _navigationEvent.send(ShoppingNavigationEvent.ErrorMessage(Res.string.shopping_list_item_name_cant_be_empty_error))
            return false
        }
        if (item.price == 0.0) {
            _navigationEvent.send(ShoppingNavigationEvent.ErrorMessage(Res.string.shopping_list_item_price_cant_be_zero))
            return false
        }
        if (item.price < 0) {
            _navigationEvent.send(ShoppingNavigationEvent.ErrorMessage(Res.string.shopping_list_item_price_cant_be_negative))
            return false
        }
        return true
    }
}
