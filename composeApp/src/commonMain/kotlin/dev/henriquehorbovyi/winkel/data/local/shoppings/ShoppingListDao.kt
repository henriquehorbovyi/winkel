package dev.henriquehorbovyi.winkel.data.local.shoppings

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Query("select * from shopping_list order by id desc")
    fun getAll(): Flow<List<ShoppingListEntity>>

    @Query("select * from shopping_list where id = :id")
    suspend fun getById(id: Long): ShoppingListEntity

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insert(shopping: ShoppingListEntity)

    @Query("update shopping_list set name = :name where id = :id")
    suspend fun update(id: Long, name: String)

    @Delete
    suspend fun delete(shopping: ShoppingListEntity)

    @Query("select * from shopping_list order by id desc limit 1")
    fun getLastInserted(): ShoppingListEntity
}