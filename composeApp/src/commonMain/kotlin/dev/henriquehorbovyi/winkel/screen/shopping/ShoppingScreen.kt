package dev.henriquehorbovyi.winkel.screen.shopping

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import dev.henriquehorbovyi.winkel.core.components.AddItemInput
import dev.henriquehorbovyi.winkel.core.components.ErrorState
import dev.henriquehorbovyi.winkel.core.components.ProgressIndicator
import dev.henriquehorbovyi.winkel.core.components.ShoppingItems
import dev.henriquehorbovyi.winkel.core.components.ShoppingListTotal
import dev.henriquehorbovyi.winkel.core.observeWithLifecycle
import dev.henriquehorbovyi.winkel.screen.shopping.data.ShoppingAction
import dev.henriquehorbovyi.winkel.screen.shopping.data.ShoppingItem
import dev.henriquehorbovyi.winkel.screen.shopping.data.ShoppingNavigationEvent
import dev.henriquehorbovyi.winkel.screen.shopping.data.ShoppingState
import dev.henriquehorbovyi.winkel.screen.shopping.viewmodel.IShoppingViewModel
import dev.henriquehorbovyi.winkel.screen.shopping.viewmodel.ShoppingViewModel
import dev.henriquehorbovyi.winkel.theme.WinkelTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import winkel.composeapp.generated.resources.Res
import winkel.composeapp.generated.resources.confirm_delete_shopping_item
import winkel.composeapp.generated.resources.no
import winkel.composeapp.generated.resources.yes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    viewModel: IShoppingViewModel = koinViewModel<ShoppingViewModel>(),
    onNavigateBack: () -> Unit = {},
    onErrorMessage: (messageRes: StringResource) -> Unit = {},
) {
    var shoppingItemToDelete: ShoppingItem? by remember { mutableStateOf(null) }

    viewModel.navigationEvent.observeWithLifecycle { event ->
        when (event) {
            is ShoppingNavigationEvent.ErrorMessage -> onErrorMessage(event.messageRes)
            is ShoppingNavigationEvent.ConfirmRemoveItem -> shoppingItemToDelete = event.item
            ShoppingNavigationEvent.NavigateBack -> onNavigateBack()
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val shoppingListName = if (uiState is ShoppingState.Content) {
        (uiState as ShoppingState.Content).data.shoppingListName
    } else {
        ""
    }
    var isEditingShoppingListName by remember { mutableStateOf(false) }
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Box {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = WinkelTheme.colors.background,
                        titleContentColor = WinkelTheme.colors.onBackground,
                        actionIconContentColor = WinkelTheme.colors.onBackground,
                        navigationIconContentColor = WinkelTheme.colors.onBackground
                    ),
                    title = {
                        if (isEditingShoppingListName) {
                            TextField(
                                value = shoppingListName,
                                onValueChange = {
                                    viewModel.onAction(ShoppingAction.OnEditShoppingListName(it))
                                },
                                textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    viewModel.onAction(
                                        ShoppingAction.SaveShoppingListName(shoppingListName)
                                    )
                                }),
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    errorContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    errorIndicatorColor = Color.Transparent,
                                ),
                            )
                        } else {
                            Text(
                                text = shoppingListName,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().clickable {
                                    viewModel.onAction(ShoppingAction.StartEditingShoppingListName)
                                }
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { padding ->
            when (val state = uiState) {
                is ShoppingState.Error -> ErrorState(state.messageResId)
                is ShoppingState.Loading -> ProgressIndicator()
                is ShoppingState.Content -> {
                    isEditingShoppingListName = state.data.isEditingShoppingListName
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
                            onStartEditing = { viewModel.onAction(ShoppingAction.EditItem(it)) },
                            onCancelEditing = { viewModel.onAction(ShoppingAction.CancelEditing(it)) },
                            onSaveEditing = { viewModel.onAction(ShoppingAction.UpdateItem(it)) },
                            onRemove = { viewModel.onAction(ShoppingAction.ConfirmRemoveItem(it)) },
                            onEditingItemChanged = { index, item ->
                                viewModel.onAction(ShoppingAction.OnEditingItemChanged(index, item))
                            }
                        )
                        AddItemInput(
                            shoppingItem = state.data.currentNewShoppingItem,
                            onShoppingItemChange = {
                                viewModel.onAction(ShoppingAction.OnShoppingItemChanged(it))
                            },
                            onSaveItem = {
                                viewModel.onAction(ShoppingAction.SaveItem(it))
                            },
                        )
                    }
                }
            }
        }
        shoppingItemToDelete?.let {
            AlertDialog(
                onDismissRequest = { shoppingItemToDelete = null },
                text = { Text(stringResource(Res.string.confirm_delete_shopping_item)) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.onAction(ShoppingAction.RemoveItem(it))
                        shoppingItemToDelete = null
                    }) { Text(stringResource(Res.string.yes)) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        shoppingItemToDelete = null
                    }) { Text(stringResource(Res.string.no)) }
                }
            )
        }
    }
}

