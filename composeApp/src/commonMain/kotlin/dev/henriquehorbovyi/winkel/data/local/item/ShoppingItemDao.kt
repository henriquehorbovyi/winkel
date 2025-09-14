package dev.henriquehorbovyi.winkel.data.local.item

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface ShoppingItemDao {
    @Query("select * from shopping_items where id = :id")
    suspend fun getItemById(id: Long): ShoppingItemEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem(item: ShoppingItemEntity)

    @Transaction
    @Update
    suspend fun updateItem(item: ShoppingItemEntity)

    @Delete
    suspend fun deleteItem(item: ShoppingItemEntity)

    @Query("delete from shopping_items where shoppingListId = :shoppingListId")
    suspend fun deleteAllItems(shoppingListId: Long)

    @Query("select * from shopping_items where shoppingListId = :shoppingListId order by id asc")
    fun getAllItemsByShopping(shoppingListId: Long): Flow<List<ShoppingItemEntity>>

    @Query("select sum(price * quantity) from shopping_items where shoppingListId = :shoppingListId and isBought = 1")
    suspend fun getTotalBoughtPrice(shoppingListId: Long): Double
}

