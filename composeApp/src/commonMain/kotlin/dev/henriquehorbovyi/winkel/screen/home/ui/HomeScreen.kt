package dev.henriquehorbovyi.winkel.screen.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.henriquehorbovyi.winkel.core.observeWithLifecycle
import dev.henriquehorbovyi.winkel.screen.home.data.HomeAction
import dev.henriquehorbovyi.winkel.screen.home.data.HomeNavigationAction
import dev.henriquehorbovyi.winkel.screen.home.data.HomeUiState
import dev.henriquehorbovyi.winkel.screen.home.data.ShoppingList
import dev.henriquehorbovyi.winkel.screen.home.viewmodel.HomeViewModel
import dev.henriquehorbovyi.winkel.screen.home.viewmodel.IHomeViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import winkel.composeapp.generated.resources.Res
import winkel.composeapp.generated.resources.ic_add_list
import winkel.composeapp.generated.resources.ic_dark_mode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: IHomeViewModel = koinInject<HomeViewModel>(),
    navigateToNewShoppingList: () -> Unit,
    navigateToShoppingList: (ShoppingList) -> Unit
) {

    viewModel.navigation.observeWithLifecycle { event ->
        when (event) {
            HomeNavigationAction.NewShoppingList -> navigateToNewShoppingList()
            is HomeNavigationAction.OpenShoppingList -> navigateToShoppingList(event.shoppingList)
        }
    }
    val uiState by viewModel.uiState.collectAsState()
    val onBackground = MaterialTheme.colorScheme.onBackground
    val background = MaterialTheme.colorScheme.background

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(horizontal = 8.dp),
                title = { Text(text = "Winkel") },
                actions = {
                    IconButton(
                        onClick = {
                            // TODO(Change theme)
                        },
                        content = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_dark_mode),
                                contentDescription = null,
                            )
                        }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = background,
                contentColor = onBackground,
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
                        onAction = viewModel::onAction
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
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    uiState: HomeUiState.Content,
    onAction: (HomeAction) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(uiState.data) { shoppingList ->
            ShoppingListCard(
                modifier = Modifier
                    .fillMaxWidth(),
                shoppingList = shoppingList,
                onClick = { onAction(HomeAction.OpenShoppingList(shoppingList)) }
            )
        }
    }
}

@Composable
fun ShoppingListCard(
    shoppingList: ShoppingList,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        Text(shoppingList.name, style = MaterialTheme.typography.bodyLarge)
    }
}