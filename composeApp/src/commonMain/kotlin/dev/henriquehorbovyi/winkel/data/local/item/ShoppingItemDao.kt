package dev.henriquehorbovyi.winkel.data.local.item

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine


@Dao
interface ShoppingItemDao {
    @Query("SELECT * FROM shopping_items WHERE id = :id")
    suspend fun getItemById(id: Long): ShoppingItemEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem(item: ShoppingItemEntity)

    @Transaction
    @Update
    suspend fun updateItem(item: ShoppingItemEntity)

    @Delete
    suspend fun deleteItem(item: ShoppingItemEntity)

    @Query("DELETE FROM shopping_items")
    suspend fun deleteAllItems()

    @Query("SELECT * FROM shopping_items WHERE shoppingListId = :shoppingListId ORDER BY id ASC")
    fun getAllItemsByShopping(shoppingListId: Long): Flow<List<ShoppingItemEntity>>

    @Query("SELECT sum(price * quantity) FROM shopping_items WHERE shoppingListId = :shoppingListId AND isBought = 1")
    suspend fun getTotalBoughtPrice(shoppingListId: Long): Double
}

