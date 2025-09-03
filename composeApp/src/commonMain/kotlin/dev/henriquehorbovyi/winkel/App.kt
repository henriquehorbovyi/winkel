package dev.henriquehorbovyi.winkel

import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.henriquehorbovyi.winkel.navigation.MainGraph
import dev.henriquehorbovyi.winkel.screen.home.ui.HomeScreen
import dev.henriquehorbovyi.winkel.screen.shopping.ShoppingScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    MaterialTheme {
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        Scaffold(
            modifier = Modifier.imePadding(),
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            content = {
                NavHost(navController, startDestination = MainGraph.Home) {

                    mainGraph(
                        navController = navController,
                        onErrorMessage = { messageRes ->
                            scope.launch {
                                val message = getString(messageRes)
                                snackBarHostState.showSnackbar(message, withDismissAction = true)
                            }
                        }
                    )
                }
            }
        )
    }
}

fun NavGraphBuilder.mainGraph(navController: NavController, onErrorMessage: (StringResource) -> Unit) {
    composable<MainGraph.Home> {
        HomeScreen(
            navigateToNewShoppingList = { navController.navigateToShopping() },
            navigateToShoppingList = { navController.navigateToShopping(it.id) }
        )
    }
    composable<MainGraph.Shopping> {
        ShoppingScreen(
            onNavigateBack = { navController.navigateUp() },
            onErrorMessage = { onErrorMessage(it) },
        )
    }
}

fun NavController.navigateToShopping(shoppingListId: Long? = null) {
    navigate(MainGraph.Shopping(shoppingListId = shoppingListId))
}