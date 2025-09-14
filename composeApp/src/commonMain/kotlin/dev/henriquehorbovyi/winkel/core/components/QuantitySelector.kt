package dev.henriquehorbovyi.winkel.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.henriquehorbovyi.winkel.screen.shopping.data.ShoppingItem
import org.jetbrains.compose.resources.painterResource
import winkel.composeapp.generated.resources.Res
import winkel.composeapp.generated.resources.ic_arrow_left
import winkel.composeapp.generated.resources.ic_arrow_right

@Composable
fun QuantitySelector(
    modifier: Modifier = Modifier,
    shoppingItem: ShoppingItem,
    onShoppingItemChange: (ShoppingItem) -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray.copy(alpha = 0.5f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(Res.drawable.ic_arrow_left),
            modifier = Modifier
                .clip(CircleShape)
                .size(32.dp)
                .clickable { onShoppingItemChange(shoppingItem.copy(quantity = shoppingItem.quantity - 1)) }
                .padding(4.dp),
            contentDescription = null
        )
        Text(
            shoppingItem.quantity.toString(),
            Modifier.padding(horizontal = 4.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Icon(
            painterResource(Res.drawable.ic_arrow_right),
            modifier = Modifier
                .clip(CircleShape)
                .size(32.dp)
                .clickable { onShoppingItemChange(shoppingItem.copy(quantity = shoppingItem.quantity + 1)) }
                .padding(4.dp),
            contentDescription = null
        )
    }
}