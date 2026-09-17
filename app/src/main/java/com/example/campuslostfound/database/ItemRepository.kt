package com.example.campuslostfound.database

class ItemRepository(
    private val itemDao: ItemDao
) {

    suspend fun insertItem(
        item: ItemEntity
    ) {
        itemDao.insertItem(item)
    }

    suspend fun updateItem(
        item: ItemEntity
    ): Int {
        return itemDao.updateItem(item)
    }

    suspend fun getAllItems(): List<ItemEntity> {
        return itemDao.getAllItems()
    }

    suspend fun getLostItems(): List<ItemEntity> {
        return itemDao.getLostItems()
    }

    suspend fun getFoundItems(): List<ItemEntity> {
        return itemDao.getFoundItems()
    }

    suspend fun searchItems(
        query: String
    ): List<ItemEntity> {
        return itemDao.searchItems(query)
    }

    suspend fun getItemByOwner(
        itemId: Int,
        userId: Int
    ): ItemEntity? {
        return itemDao.getItemByOwner(
            itemId,
            userId
        )
    }

    suspend fun updateItemByOwner(
        itemId: Int,
        userId: Int,
        name: String,
        description: String,
        category: String,
        location: String,
        date: String
    ): Int {

        return itemDao.updateItemByOwner(
            itemId,
            userId,
            name,
            description,
            category,
            location,
            date
        )
    }

    suspend fun deleteItemByOwner(
        itemId: Int,
        userId: Int
    ): Int {

        return itemDao.deleteItemByOwner(
            itemId,
            userId
        )
    }
}