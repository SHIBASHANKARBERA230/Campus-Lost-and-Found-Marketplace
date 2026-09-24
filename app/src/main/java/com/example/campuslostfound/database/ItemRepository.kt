package com.example.campuslostfound.database

class ItemRepository(
    private val itemDao: ItemDao
) {

    suspend fun insertItem(
        item: ItemEntity
    ): Long {
        return itemDao.insertItem(item)
    }

    suspend fun updateItem(
        item: ItemEntity
    ): Int {
        return itemDao.updateItem(item)
    }

    suspend fun getItemById(
        itemId: Int
    ): ItemEntity? {
        return itemDao.getItemById(itemId)
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

    suspend fun getItemsByCategory(
        category: String
    ): List<ItemEntity> {
        return itemDao.getItemsByCategory(category)
    }

    suspend fun getItemsByTypeAndCategory(
        type: String,
        category: String
    ): List<ItemEntity> {
        return itemDao.getItemsByTypeAndCategory(
            type,
            category
        )
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

    suspend fun deleteItemByOwner(
        itemId: Int,
        userId: Int
    ): Int {
        return itemDao.deleteItemByOwner(
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

    suspend fun updateItemStatusByOwner(
        itemId: Int,
        userId: Int,
        status: String
    ): Int {

        return itemDao.updateItemStatusByOwner(
            itemId = itemId,
            userId = userId,
            status = status
        )
    }

    suspend fun getItemsByUser(
        userId: Int
    ): List<ItemEntity> {
        return itemDao.getItemsByUser(userId)
    }

    suspend fun findMatchingItems(
        oppositeType: String,
        category: String,
        name: String,
        userId: Int
    ): List<ItemEntity> {

        return itemDao.findMatchingItems(
            oppositeType = oppositeType,
            category = category,
            name = name,
            userId = userId
        )
    }
}