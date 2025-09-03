package dev.henriquehorbovyi.winkel.screen.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.henriquehorbovyi.winkel.data.repository.IShoppingRepository
import dev.henriquehorbovyi.winkel.screen.home.data.HomeAction
import dev.henriquehorbovyi.winkel.screen.home.data.HomeNavigationAction
import dev.henriquehorbovyi.winkel.screen.home.data.HomeUiState
import dev.henriquehorbovyi.winkel.screen.home.data.ShoppingList
import dev.henriquehorbovyi.winkel.screen.home.data.toEntity
import dev.henriquehorbovyi.winkel.screen.home.data.toUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface IHomeViewModel {
    val navigation: Flow<HomeNavigationAction>
    val uiState: StateFlow<HomeUiState>

    fun onAction(action: HomeAction)
}

class HomeViewModel(
    private val shoppingRepository: IShoppingRepository
) : IHomeViewModel, ViewModel() {

    override val navigation = MutableSharedFlow<HomeNavigationAction>(extraBufferCapacity = 1)
    override val uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    init {
        viewModelScope.launch {
            shoppingRepository.getAllShoppings().collect { shoppingLists ->
                uiState.value = HomeUiState.Content(shoppingLists.map { it.toUi() })
            }
        }
    }

    override fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.DeleteShoppingList -> handleDeleteShoppingList(action.shoppingList)
            is HomeAction.OpenShoppingList -> handleOpenShoppingList(action.shoppingList)
            HomeAction.NewShoppingList -> navigation.tryEmit(HomeNavigationAction.NewShoppingList)
        }
    }

    private fun handleOpenShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch {
            navigation.emit(HomeNavigationAction.OpenShoppingList(shoppingList))
        }
    }

    private fun handleDeleteShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch {
            shoppingRepository.deleteShopping(shoppingList.toEntity())
        }
    }
}