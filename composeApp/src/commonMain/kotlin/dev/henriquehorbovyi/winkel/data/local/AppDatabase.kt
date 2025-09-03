package dev.henriquehorbovyi.winkel.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import dev.henriquehorbovyi.winkel.data.local.item.ShoppingItemDao
import dev.henriquehorbovyi.winkel.data.local.item.ShoppingItemEntity
import dev.henriquehorbovyi.winkel.data.local.shoppings.ShoppingListDao
import dev.henriquehorbovyi.winkel.data.local.shoppings.ShoppingListEntity

internal const val dbFileName = "app_room_db.db"

@Database(
    entities = [ShoppingItemEntity::class, ShoppingListEntity::class],
    version = 1,
    exportSchema = true
)
@ConstructedBy(DatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoppingListDao(): ShoppingListDao
   abstract fun shoppingItemDao(): ShoppingItemDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object DatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

expect class DatabaseFactory {
    fun createDatabase(): AppDatabase
}