package dev.henriquehorbovyi.winkel

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.henriquehorbovyi.winkel.navigation.MainGraph
import dev.henriquehorbovyi.winkel.screen.home.ui.HomeScreen
import dev.henriquehorbovyi.winkel.screen.shopping.ShoppingScreen
import dev.henriquehorbovyi.winkel.theme.WinkelTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val appState = rememberAppState()

    WinkelTheme(darkTheme = appState.isDarkMode) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            snackbarHost = { SnackbarHost(hostState = appState.snackBarHostState) },
            content = {
                NavHost(appState.navController, startDestination = MainGraph.Home) {

                    mainGraph(
                        navController = appState.navController,
                        onErrorMessage = { messageRes -> appState.launchSnackBar(messageRes) }
                    )
                }
            }
        )
    }
}

fun NavGraphBuilder.mainGraph(
    navController: NavController,
    onErrorMessage: (StringResource) -> Unit
) {
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