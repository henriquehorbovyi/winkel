package dev.henriquehorbovyi.winkel.screen.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.henriquehorbovyi.winkel.data.repository.IShoppingRepository
import dev.henriquehorbovyi.winkel.data.repository.PreferencesRepository
import dev.henriquehorbovyi.winkel.screen.home.data.HomeAction
import dev.henriquehorbovyi.winkel.screen.home.data.HomeContent
import dev.henriquehorbovyi.winkel.screen.home.data.HomeNavigationAction
import dev.henriquehorbovyi.winkel.screen.home.data.HomeUiState
import dev.henriquehorbovyi.winkel.screen.home.data.ShoppingList
import dev.henriquehorbovyi.winkel.screen.home.data.toEntity
import dev.henriquehorbovyi.winkel.screen.home.data.toUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface IHomeViewModel {
    val navigation: Channel<HomeNavigationAction>
    val uiState: StateFlow<HomeUiState>

    fun onAction(action: HomeAction)
}

class HomeViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val shoppingRepository: IShoppingRepository
) : IHomeViewModel, ViewModel() {

    override val navigation = Channel<HomeNavigationAction>()
    override val uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    init {
        viewModelScope.launch {
            combine(
                preferencesRepository.isDarkMode,
                shoppingRepository.getAllShoppings()
            ) { isDarkMode, shoppingLists ->
                val content = HomeContent(
                    shoppingLists = shoppingLists.map { it.toUi() },
                    isDarkMode = isDarkMode
                )
                HomeUiState.Content(content = content)
            }.collect { state ->
                uiState.update { state }
            }
        }
    }

    override fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.ConfirmDeleteShoppingList -> confirmDeleteShoppingList(action.shoppingList)
            is HomeAction.DeleteShoppingList -> deleteShoppingList(action.shoppingList)
            is HomeAction.OpenShoppingList -> openShoppingList(action.shoppingList)
            HomeAction.NewShoppingList -> newShoppingList()
            HomeAction.ToggleTheme -> toggleTheme()
        }
    }

    private fun toggleTheme() {
        viewModelScope.launch {
            preferencesRepository.toggleTheme()
        }
    }
    private fun newShoppingList() {
        viewModelScope.launch {
            navigation.send(HomeNavigationAction.NewShoppingList)
        }
    }

    private fun openShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch {
            navigation.send(HomeNavigationAction.OpenShoppingList(shoppingList))
        }
    }

    private fun confirmDeleteShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch {
            navigation.send(HomeNavigationAction.ConfirmDeleteShoppingList(shoppingList))
        }
    }

    private fun deleteShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch {
            shoppingRepository.deleteShopping(shoppingList.toEntity())
        }
    }
}