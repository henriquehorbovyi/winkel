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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
    onSaveItem: (ShoppingItem) -> Unit,
    formShouldBeCleared: Boolean,
) {
    var name by remember { mutableStateOf("") }
    var price: String? by remember { mutableStateOf(null) }
    var quantity: String by remember { mutableStateOf("1") }

    LaunchedEffect(formShouldBeCleared) {
        if (formShouldBeCleared) {
            name = ""
            price = ""
            quantity = "1"
        }
    }

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
                    value = name,
                    onValueChange = { name = it },
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
                    colors =
                        TextFieldDefaults.colors(
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

                TextField(
                    value = price.orEmpty(),
                    onValueChange = { price = it },
                    placeholder = { Text("$0,00") },
                    label = {
                        Text(
                            "Price",
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions =
                        KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Number,
                        ),
                    singleLine = true,
                    colors =
                        TextFieldDefaults.colors(
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

                Row(
                    modifier = Modifier
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
                            .clickable {
                                quantity = (quantity.toIntOrNull() ?: 0).minus(1).toString()
                            }
                            .padding(4.dp),
                        contentDescription = null
                    )
                    Text(quantity, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                    Icon(
                        painterResource(Res.drawable.ic_arrow_right),
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(32.dp)
                            .clickable {
                                quantity = (quantity.toIntOrNull() ?: 0).plus(1).toString()
                            }
                            .padding(4.dp),
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                onClick = {
                    onSaveItem(
                        ShoppingItem(
                            name = name,
                            price = price?.toDoubleOrNull() ?: 0.0,
                            quantity = quantity.toInt(),
                            isBought = false,
                        )
                    )
                },
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

