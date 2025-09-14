package dev.henriquehorbovyi.winkel.core.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.henriquehorbovyi.winkel.screen.shopping.data.ShoppingData
import dev.henriquehorbovyi.winkel.screen.shopping.data.ShoppingItem
import org.jetbrains.compose.resources.painterResource
import winkel.composeapp.generated.resources.Res
import winkel.composeapp.generated.resources.ic_edit

@Composable
fun ShoppingItems(
    modifier: Modifier = Modifier,
    shoppingData: ShoppingData,
    markAsBought: (item: ShoppingItem, isBought: Boolean) -> Unit,
    onEditingItemChanged: (index: Int, item: ShoppingItem) -> Unit,
    onSaveEditing: (ShoppingItem) -> Unit,
    onCancelEditing: (itemIndex: Int) -> Unit,
    onStartEditing: (itemIndex: Int) -> Unit,
    onRemove: (ShoppingItem) -> Unit,
) {
    val listState = rememberLazyListState()
    val lastItem = remember { mutableStateOf(shoppingData.items.lastOrNull()) }

    LaunchedEffect(shoppingData.items) {
        if (lastItem.value != shoppingData.items.lastOrNull() && shoppingData.items.isNotEmpty()) {
            listState.animateScrollToItem(shoppingData.items.size - 1)
            lastItem.value = shoppingData.items.lastOrNull()
        }
    }
    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        itemsIndexed(shoppingData.items) { index, item ->
            if (shoppingData.editingShoppingItem != null && item.isEditing) {
                ShoppingEditItem(
                    item = shoppingData.editingShoppingItem,
                    onUpdate = { onSaveEditing(it) },
                    onItemChange = { onEditingItemChanged(index, it) },
                    onCancelEditing = { onCancelEditing(index) },
                )
            } else {
                ShoppingItem(
                    item = item,
                    onCheckedChange = { checked -> markAsBought(item, checked) },
                    onRemove = { onRemove(item) },
                    onStartEditing = { onStartEditing(index) },
                )
            }
        }
    }
}

@Composable
private fun ShoppingItem(
    modifier: Modifier = Modifier,
    item: ShoppingItem,
    onCheckedChange: (Boolean) -> Unit,
    onStartEditing: (ShoppingItem) -> Unit,
    onRemove: (ShoppingItem) -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { onCheckedChange(!item.isBought) },
                    onLongClick = { onRemove(item) },
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = item.isBought, onCheckedChange = { onCheckedChange(!item.isBought) })
        Spacer(modifier = Modifier.width(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text("${item.quantity} x ${item.name}")
                Text(
                    text = item.maskedPrice,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { onStartEditing(item) },
                content = {
                    Icon(
                        painter = painterResource(resource = Res.drawable.ic_edit),
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
private fun ShoppingEditItem(
    modifier: Modifier = Modifier,
    item: ShoppingItem,
    onUpdate: (ShoppingItem) -> Unit,
    onItemChange: (ShoppingItem) -> Unit,
    onCancelEditing: (ShoppingItem) -> Unit,
) {
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
                value = item.name,
                onValueChange = { onItemChange(item.copy(name = it)) },
                placeholder = { Text("item") },
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

            MoneyInput(
                value = item.maskedPrice,
                onValueChange = { onItemChange(item.copy(maskedPrice = it)) }
            )
            QuantitySelector(
                shoppingItem = item,
                onShoppingItemChange = { onItemChange(it) }
            )

        }
        Row {
            TextButton(
                onClick = { onUpdate(item) },
                content = { Text("Save", style = MaterialTheme.typography.labelMedium) },
            )
            TextButton(
                modifier = Modifier.padding(start = 8.dp),
                onClick = { onCancelEditing(item) },
                content = { Text("Cancel", style = MaterialTheme.typography.labelMedium) },
            )
        }
    }
}
