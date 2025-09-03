package dev.henriquehorbovyi.winkel.screen.home.data

import dev.henriquehorbovyi.winkel.data.local.shoppings.ShoppingListEntity

data class ShoppingList(
    val id: Long,
    val name: String,
)

fun ShoppingListEntity.toUi() = ShoppingList(
    id = id,
    name = name,
)

fun ShoppingList.toEntity() = ShoppingListEntity(
    id = id,
    name = name,
)