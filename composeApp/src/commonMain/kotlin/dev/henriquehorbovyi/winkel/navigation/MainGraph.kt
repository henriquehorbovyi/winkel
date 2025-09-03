package dev.henriquehorbovyi.winkel.navigation

import kotlinx.serialization.Serializable

@Serializable object MainGraph {
    @Serializable object Home
    @Serializable data class Shopping(val shoppingListId: Long?)
}
