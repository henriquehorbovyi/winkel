package dev.henriquehorbovyi.winkel.core.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.henriquehorbovyi.winkel.screen.shopping.data.ShoppingData
import dev.henriquehorbovyi.winkel.screen.shopping.data.ShoppingItem

@Composable
fun ShoppingItems(
    modifier: Modifier = Modifier,
    shoppingData: ShoppingData,
    markAsBought: (item: ShoppingItem, isBought: Boolean) -> Unit,
    onAddItem: () -> Unit = {},
    onCancelEditing: () -> Unit,
    onSaveEditing: (ShoppingItem) -> Unit,
    onRemove: (ShoppingItem) -> Unit,
) {
    var isEditing by remember { mutableStateOf(false) }

    LazyColumn(modifier = modifier) {
        items(shoppingData.items) { item ->
            if (item.isTemporary) {
                isEditing = true
                ShoppingEditItem(
                    item = item,
                    onUpdate = {
                        isEditing = false
                        onSaveEditing(it)
                    },
                    onCancelEditing = {
                        isEditing = false
                        onCancelEditing()
                    },
                )
            } else {
                ShoppingItem(
                    item = item,
                    onCheckedChange = { checked -> markAsBought(item, checked) },
                )
            }
        }

        /* item {
             // add item
             Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp)) {
                 TextButton(
                     content = { Text("Add more...") },
                     onClick = {
                         if (!isEditing) {
                             onAddItem()
                         } else {
                             // TODO: trigger action to show that user needs to finish editing one item at time
                         }
                     },
                 )
             }
         }*/
    }
}

@Composable
private fun ShoppingItem(
    modifier: Modifier = Modifier,
    item: ShoppingItem,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onCheckedChange(!item.isBought) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = item.isBought, onCheckedChange = { onCheckedChange(!item.isBought) })
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text("${item.quantity} x ${item.name}")
            Text(
                text = "R$ ${item.price}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
            )
        }
    }
}

@Composable
private fun ShoppingEditItem(
    modifier: Modifier = Modifier,
    item: ShoppingItem,
    onUpdate: (ShoppingItem) -> Unit,
    onCancelEditing: () -> Unit,
) {
    var name by remember { mutableStateOf(item.name) }
    var price: String by remember { mutableStateOf(item.price.toString()) }
    var quantity: String by remember { mutableStateOf(item.quantity.toString()) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("product name") },
                modifier =
                    Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
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
                value = price,
                onValueChange = { price = it },
                placeholder = { Text("$0,00") },
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

            TextField(
                value = quantity,
                onValueChange = { quantity = it },
                placeholder = { Text("1") },
                modifier = Modifier.weight(1f),
                keyboardOptions =
                    KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number,
                    ),
                keyboardActions =
                    KeyboardActions(
                        onDone = {
                            onUpdate(
                                item.copy(
                                    name = name,
                                    price = price.toDoubleOrNull() ?: 0.0,
                                    quantity = quantity.toIntOrNull() ?: 0,
                                    isTemporary = false,
                                ),
                            )
                        },
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
        }
        Row {
            TextButton(
                onClick = {
                    onUpdate(
                        item.copy(
                            name = name,
                            price = price.toDoubleOrNull() ?: 0.0,
                            quantity = quantity.toIntOrNull() ?: 0,
                            isTemporary = false,
                        ),
                    )
                },
                content = { Text("Save", style = MaterialTheme.typography.labelMedium) },
            )
            TextButton(
                modifier = Modifier.padding(start = 8.dp),
                onClick = { onCancelEditing() },
                content = { Text("Cancel", style = MaterialTheme.typography.labelMedium) },
            )
        }
    }
}
