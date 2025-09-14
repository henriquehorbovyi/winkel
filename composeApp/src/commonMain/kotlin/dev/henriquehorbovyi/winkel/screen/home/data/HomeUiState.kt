package dev.henriquehorbovyi.winkel.screen.home.data

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Content(val content: HomeContent) : HomeUiState
    data class Error(val exception: Exception) : HomeUiState
}

data class HomeContent(
    val shoppingLists: List<ShoppingList>,
    val isDarkMode: Boolean
)

sealed interface HomeNavigationAction {
    object NewShoppingList : HomeNavigationAction
    data class OpenShoppingList(val shoppingList: ShoppingList) : HomeNavigationAction
    data class ConfirmDeleteShoppingList(val shoppingList: ShoppingList) : HomeNavigationAction
}

sealed interface HomeAction {
    // demo action
    object NewShoppingList : HomeAction
    object ToggleTheme : HomeAction
    data class OpenShoppingList(val shoppingList: ShoppingList) : HomeAction
    data class DeleteShoppingList(val shoppingList: ShoppingList) : HomeAction
    data class ConfirmDeleteShoppingList(val shoppingList: ShoppingList) : HomeAction
}

