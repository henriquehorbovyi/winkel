package dev.henriquehorbovyi.winkel

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dev.henriquehorbovyi.winkel.core.PreferenceViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun rememberAppState(
    preferencesViewModel: PreferenceViewModel = koinViewModel(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
): AppState = remember(
    coroutineScope,
    navController,
    snackBarHostState,
) {
    AppState(
        coroutineScope = coroutineScope,
        navController = navController,
        snackBarHostState = snackBarHostState,
        preferencesViewModel = preferencesViewModel,
    )
}

@Stable
class AppState(
    val navController: NavHostController,
    val snackBarHostState: SnackbarHostState,
    private val coroutineScope: CoroutineScope,
    private val preferencesViewModel: PreferenceViewModel,
) {
    val isDarkMode: Boolean
        @Composable get() = preferencesViewModel.isDarkMode.collectAsState().value

    fun launchSnackBar(
        messageRes: StringResource,
        dismissable: Boolean = true,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onDismiss: () -> Unit = {}
    ) {
        coroutineScope.launch {
            val result = snackBarHostState.showSnackbar(
                message = getString(messageRes),
                duration = duration,
                withDismissAction = dismissable
            )
            if (result == SnackbarResult.Dismissed) onDismiss.invoke()
        }
    }
}
