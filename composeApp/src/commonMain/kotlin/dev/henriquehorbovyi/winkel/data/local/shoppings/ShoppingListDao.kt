package dev.henriquehorbovyi.winkel.data.local.shoppings

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM shopping_list")
    fun getAll(): Flow<List<ShoppingListEntity>>

    @Query("SELECT * FROM shopping_list WHERE id = :id")
    fun getById(id: Long): ShoppingListEntity

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    fun insert(shopping: ShoppingListEntity)

    @Update
    fun update(shopping: ShoppingListEntity)

    @Delete
    fun delete(shopping: ShoppingListEntity)

    @Query("select * from shopping_list order by id desc limit 1")
    fun getLastInserted(): ShoppingListEntity
}