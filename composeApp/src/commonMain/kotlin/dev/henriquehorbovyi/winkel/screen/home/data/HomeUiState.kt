package dev.henriquehorbovyi.winkel.screen.home.data

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Content(val data: List<ShoppingList>) : HomeUiState
    data class Error(val exception: Exception) : HomeUiState
}

sealed interface HomeNavigationAction {
    object NewShoppingList : HomeNavigationAction
    data class OpenShoppingList(val shoppingList: ShoppingList) : HomeNavigationAction
}

sealed interface HomeAction {
    // demo action
    object NewShoppingList : HomeAction
    data class OpenShoppingList(val shoppingList: ShoppingList) : HomeAction
    data class DeleteShoppingList(val shoppingList: ShoppingList) : HomeAction
}

