package dev.henriquehorbovyi.winkel.screen.home.ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.henriquehorbovyi.winkel.core.observeWithLifecycle
import dev.henriquehorbovyi.winkel.screen.home.data.HomeAction
import dev.henriquehorbovyi.winkel.screen.home.data.HomeNavigationAction
import dev.henriquehorbovyi.winkel.screen.home.data.HomeUiState
import dev.henriquehorbovyi.winkel.screen.home.data.ShoppingList
import dev.henriquehorbovyi.winkel.screen.home.viewmodel.HomeViewModel
import dev.henriquehorbovyi.winkel.screen.home.viewmodel.IHomeViewModel
import dev.henriquehorbovyi.winkel.theme.WinkelTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import winkel.composeapp.generated.resources.Res
import winkel.composeapp.generated.resources.confirm_delete_shopping_list
import winkel.composeapp.generated.resources.ic_add_list
import winkel.composeapp.generated.resources.ic_dark_mode
import winkel.composeapp.generated.resources.ic_light_mode
import winkel.composeapp.generated.resources.no
import winkel.composeapp.generated.resources.yes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: IHomeViewModel = koinInject<HomeViewModel>(),
    navigateToNewShoppingList: () -> Unit,
    navigateToShoppingList: (ShoppingList) -> Unit
) {
    var shoppingListToDelete: ShoppingList? by remember { mutableStateOf(null) }

    viewModel.navigation.observeWithLifecycle { event ->
        when (event) {
            HomeNavigationAction.NewShoppingList -> navigateToNewShoppingList()
            is HomeNavigationAction.OpenShoppingList -> navigateToShoppingList(event.shoppingList)
            is HomeNavigationAction.ConfirmDeleteShoppingList -> shoppingListToDelete =
                event.shoppingList
        }
    }
    val uiState by viewModel.uiState.collectAsState()

    Box {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = WinkelTheme.colors.onBackground,
                        actionIconContentColor = WinkelTheme.colors.onBackground,
                        navigationIconContentColor = WinkelTheme.colors.onBackground
                    ),
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    title = { Text(text = "Winkel") },
                    actions = {
                        IconButton(
                            onClick = { viewModel.onAction(HomeAction.ToggleTheme) },
                            content = {
                                val state = uiState as? HomeUiState.Content
                                val icon = if (state != null && state.content.isDarkMode) {
                                    Res.drawable.ic_dark_mode
                                } else {
                                    Res.drawable.ic_light_mode
                                }
                                Icon(
                                    painter = painterResource(icon),
                                    contentDescription = null,
                                )
                            }
                        )
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    containerColor = WinkelTheme.colors.primary,
                    contentColor = WinkelTheme.colors.onPrimary,
                    onClick = { viewModel.onAction(HomeAction.NewShoppingList) }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_add_list),
                        null
                    )
                }
            },
            content = { padding ->
                when (val state = uiState) {
                    is HomeUiState.Content -> {
                        Content(
                            modifier = Modifier.padding(padding),
                            uiState = state,
                            onAction = viewModel::onAction,
                        )
                    }

                    is HomeUiState.Loading -> {
                        // Show loading indicator
                    }

                    is HomeUiState.Error -> {
                        // Show error message
                    }
                }
            }
        )
        shoppingListToDelete?.let {
            AlertDialog(
                onDismissRequest = { shoppingListToDelete = null },
                text = { Text(stringResource(Res.string.confirm_delete_shopping_list)) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.onAction(HomeAction.DeleteShoppingList(it))
                        shoppingListToDelete = null
                    }) { Text(stringResource(Res.string.yes)) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        shoppingListToDelete = null
                    }) { Text(stringResource(Res.string.no)) }
                }
            )
        }
    }

}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    uiState: HomeUiState.Content,
    onAction: (HomeAction) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        items(uiState.content.shoppingLists) { shoppingList ->
            ShoppingListCard(
                modifier = Modifier.fillMaxWidth(),
                shoppingList = shoppingList,
                onClick = { onAction(HomeAction.OpenShoppingList(shoppingList)) },
                onLongClick = { onAction(HomeAction.ConfirmDeleteShoppingList(shoppingList)) }
            )
        }
    }
}

@Composable
fun ShoppingListCard(
    shoppingList: ShoppingList,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = modifier
            .combinedClickable(
                onLongClick = onLongClick,
                onClick = onClick
            )
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        Text(shoppingList.name, style = MaterialTheme.typography.bodyLarge)
    }
}