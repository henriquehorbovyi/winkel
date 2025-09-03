package dev.henriquehorbovyi.winkel.data.repository

import dev.henriquehorbovyi.winkel.data.local.item.ShoppingItemDao
import dev.henriquehorbovyi.winkel.data.local.item.ShoppingItemEntity
import dev.henriquehorbovyi.winkel.data.local.item.ShoppingItemWithTotalPrice
import dev.henriquehorbovyi.winkel.data.local.shoppings.ShoppingListDao
import dev.henriquehorbovyi.winkel.data.local.shoppings.ShoppingListEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface IShoppingRepository {
    // Shopping
    suspend fun getAllShoppings(): Flow<List<ShoppingListEntity>>
    suspend fun getById(shoppingId: Long): ShoppingListEntity
    suspend fun saveShopping(shoppingList: ShoppingListEntity)
    suspend fun updateShopping(shoppingList: ShoppingListEntity)
    suspend fun deleteShopping(shoppingList: ShoppingListEntity)

    suspend fun lastInsertedShopping(): ShoppingListEntity

    // Shopping Items
    suspend fun getAllItemsWithTotalBoughtPrice(shoppingId: Long): Flow<ShoppingItemWithTotalPrice>
    suspend fun saveShoppingItem(item: ShoppingItemEntity)
    suspend fun updateShoppingItem(item: ShoppingItemEntity)
    suspend fun deleteShoppingItem(item: ShoppingItemEntity)
}

class ShoppingRepository(
    private val shoppingListDao: ShoppingListDao,
    private val shoppingItemDao: ShoppingItemDao,
) : IShoppingRepository {

    override suspend fun getAllShoppings(): Flow<List<ShoppingListEntity>> {
        return withContext(Dispatchers.IO) {
            shoppingListDao.getAll()
        }
    }

    override suspend fun getById(shoppingId: Long): ShoppingListEntity =
        withContext(Dispatchers.IO) {
            shoppingListDao.getById(shoppingId)
        }

    override suspend fun saveShopping(shoppingList: ShoppingListEntity) =
        withContext(Dispatchers.IO) {
            shoppingListDao.insert(shoppingList)
        }

    override suspend fun updateShopping(shoppingList: ShoppingListEntity) =
        withContext(Dispatchers.IO) {
            shoppingListDao.update(shoppingList)
        }

    override suspend fun deleteShopping(shoppingList: ShoppingListEntity) =
        withContext(Dispatchers.IO) {
            shoppingListDao.delete(shoppingList)
        }

    override suspend fun lastInsertedShopping(): ShoppingListEntity = withContext(Dispatchers.IO) {
        shoppingListDao.getLastInserted()
    }

    override suspend fun getAllItemsWithTotalBoughtPrice(shoppingId: Long): Flow<ShoppingItemWithTotalPrice> =
        withContext(Dispatchers.IO) {
            shoppingItemDao.getAllItemsByShopping(shoppingId).map {
                val totalBoughtPrice = shoppingItemDao.getTotalBoughtPrice(shoppingId)
                ShoppingItemWithTotalPrice(it, totalBoughtPrice)
            }
        }

    override suspend fun saveShoppingItem(item: ShoppingItemEntity) = withContext(Dispatchers.IO) {
        shoppingItemDao.insertItem(item)
    }

    override suspend fun updateShoppingItem(item: ShoppingItemEntity) =
        withContext(Dispatchers.IO) {
            shoppingItemDao.updateItem(item)
        }

    override suspend fun deleteShoppingItem(item: ShoppingItemEntity) =
        withContext(Dispatchers.IO) {
            shoppingItemDao.deleteItem(item)
        }
}
