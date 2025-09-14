package dev.henriquehorbovyi.winkel.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

internal val LocalShapes = staticCompositionLocalOf {
    Shapes(
        extraSmall = RoundedCornerShape(size = 4.dp),
        small = RoundedCornerShape(size = 8.dp),
        medium = RoundedCornerShape(size = 16.dp),
        large = RoundedCornerShape(size = 24.dp),
        extraLarge = RoundedCornerShape(32.dp)
    )
}