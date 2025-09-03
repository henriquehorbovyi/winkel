package dev.henriquehorbovyi.winkel.di

import androidx.lifecycle.SavedStateHandle
import dev.henriquehorbovyi.winkel.data.local.AppDatabase
import dev.henriquehorbovyi.winkel.data.local.item.ShoppingItemDao
import dev.henriquehorbovyi.winkel.data.local.shoppings.ShoppingListDao
import dev.henriquehorbovyi.winkel.data.repository.IShoppingRepository
import dev.henriquehorbovyi.winkel.data.repository.ShoppingRepository
import dev.henriquehorbovyi.winkel.screen.home.viewmodel.HomeViewModel
import dev.henriquehorbovyi.winkel.screen.shopping.viewmodel.ShoppingViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val commonModule = module {
    single<ShoppingItemDao> { get<AppDatabase>().shoppingItemDao() }
    single<ShoppingListDao> { get<AppDatabase>().shoppingListDao() }
    single<IShoppingRepository> { ShoppingRepository(get(), get()) }
    viewModel { (savedStateHandle: SavedStateHandle) -> ShoppingViewModel(savedStateHandle, get<IShoppingRepository>()) }
    viewModel { HomeViewModel(get<IShoppingRepository>()) }
}

expect fun platformModule(): Module
