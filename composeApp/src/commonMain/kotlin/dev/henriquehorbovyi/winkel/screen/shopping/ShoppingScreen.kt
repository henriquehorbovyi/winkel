package dev.henriquehorbovyi.winkel.screen.shopping

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.sp
import dev.henriquehorbovyi.winkel.core.components.AddItemInput
import dev.henriquehorbovyi.winkel.core.components.ShoppingItems
import dev.henriquehorbovyi.winkel.core.components.ShoppingListTotal
import dev.henriquehorbovyi.winkel.core.observeWithLifecycle
import dev.henriquehorbovyi.winkel.screen.shopping.data.ShoppingAction
import dev.henriquehorbovyi.winkel.screen.shopping.data.ShoppingNavigationEvent
import dev.henriquehorbovyi.winkel.screen.shopping.data.ShoppingState
import dev.henriquehorbovyi.winkel.screen.shopping.viewmodel.IShoppingViewModel
import dev.henriquehorbovyi.winkel.screen.shopping.viewmodel.ShoppingViewModel
import org.jetbrains.compose.resources.StringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    viewModel: IShoppingViewModel = koinViewModel<ShoppingViewModel>(),
    onNavigateBack: () -> Unit = {},
    onErrorMessage: (messageRes: StringResource) -> Unit = {},
) {

    var formShouldBeCleared by remember { mutableStateOf(false) }

    viewModel.navigationEvent.observeWithLifecycle { event ->
        when (event) {
            ShoppingNavigationEvent.ClearForm -> {
                formShouldBeCleared = true
            }

            is ShoppingNavigationEvent.ErrorMessage -> onErrorMessage(event.messageRes)
            ShoppingNavigationEvent.NavigateBack -> onNavigateBack()
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    var shoppingListName by remember { mutableStateOf("") }
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(shoppingListName, fontSize = 22.sp) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        when (val state = uiState) {
            is ShoppingState.Error -> {
                Text("Error")
            }

            is ShoppingState.Loading -> {
                Text("Loading")
            }

            is ShoppingState.Content -> {
                shoppingListName = state.data.shoppingListName
                Column(
                    modifier = Modifier.imePadding().padding(padding),
                ) {
                    ShoppingListTotal(
                        modifier = Modifier,
                        totalPrice = state.data.totalPrice,
                    )
                    ShoppingItems(
                        modifier = Modifier.weight(1f),
                        shoppingData = state.data,
                        markAsBought = { item, isBought ->
                            viewModel.onAction(ShoppingAction.MarkAsBought(item, isBought))
                        },
                        onAddItem = { viewModel.onAction(ShoppingAction.AddItem) },
                        onCancelEditing = { viewModel.onAction(ShoppingAction.CancelEditing) },
                        onSaveEditing = { viewModel.onAction(ShoppingAction.SaveItem(it)) },
                        onRemove = { viewModel.onAction(ShoppingAction.RemoveItem(it)) },
                    )
                    AddItemInput(
                        onSaveItem = { viewModel.onAction(ShoppingAction.SaveItem(it)) },
                        formShouldBeCleared = formShouldBeCleared
                    )
                }
            }
        }
    }
}

