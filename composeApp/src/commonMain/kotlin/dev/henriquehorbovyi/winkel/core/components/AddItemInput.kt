package dev.henriquehorbovyi.winkel.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.henriquehorbovyi.winkel.screen.shopping.data.ShoppingItem
import org.jetbrains.compose.resources.painterResource
import winkel.composeapp.generated.resources.Res
import winkel.composeapp.generated.resources.ic_arrow_left
import winkel.composeapp.generated.resources.ic_arrow_right
import winkel.composeapp.generated.resources.ic_send

@Composable
fun AddItemInput(
    modifier: Modifier = Modifier,
    shoppingItem: ShoppingItem,
    onShoppingItemChange: (ShoppingItem) -> Unit,
    onSaveItem: (ShoppingItem) -> Unit,
) {
    Column(modifier = modifier.padding(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray.copy(alpha = 0.3f))
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextField(
                    value = shoppingItem.name,
                    onValueChange = { onShoppingItemChange(shoppingItem.copy(name = it)) },
                    label = {
                        Text(
                            "Item",
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                        )
                    },
                    placeholder = { Text("bread \uD83C\uDF5E") },
                    modifier =
                        Modifier
                            .weight(1f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
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
                MoneyInput(
                    value = shoppingItem.maskedPrice,
                    onValueChange = { onShoppingItemChange(shoppingItem.copy(maskedPrice = it)) }
                )
                QuantitySelector(
                    shoppingItem = shoppingItem,
                    onShoppingItemChange = onShoppingItemChange
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                onClick = { onSaveItem(shoppingItem) },
                content = {
                    Icon(
                        painterResource(Res.drawable.ic_send),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Add"
                    )
                }
            )
        }
    }
}

