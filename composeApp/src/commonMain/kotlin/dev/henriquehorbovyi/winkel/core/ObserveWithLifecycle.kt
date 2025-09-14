package dev.henriquehorbovyi.winkel.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Composable
inline fun <reified T> Flow<T>.observeWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.CREATED,
    noinline action: suspend (T) -> Unit,
) {
    val onAction by rememberUpdatedState(action)
    LaunchedEffect(key1 = Unit) {
        lifecycleOwner.lifecycleScope.launch {
            flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState).collectLatest(onAction)
        }
    }
}

@Composable
inline fun <reified T> Channel<T>.observeWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.CREATED,
    noinline action: suspend (T) -> Unit,
) {
    val onAction by rememberUpdatedState(action)
    LaunchedEffect(key1 = Unit) {
        lifecycleOwner.lifecycleScope.launch {
            receiveAsFlow()
                .flowWithLifecycle(
                    lifecycle = lifecycleOwner.lifecycle,
                    minActiveState = minActiveState
                ).collectLatest(onAction)
        }
    }
}

