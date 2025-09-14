package dev.henriquehorbovyi.winkel.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

object WinkelTheme {
    val colors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalColorScheme.current

    val shapes: Shapes
        @ReadOnlyComposable
        @Composable
        get() = LocalShapes.current
}

@Composable
fun WinkelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) { darkScheme } else { lightScheme }

    CompositionLocalProvider(
        LocalColorScheme provides colorScheme,
        LocalShapes provides MaterialTheme.shapes
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
