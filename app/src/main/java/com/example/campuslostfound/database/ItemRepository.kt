package com.example.campuslostfound.database

class ItemRepository(
    private val itemDao: ItemDao
) {

    // Insert item
    suspend fun insertItem(
        item: ItemEntity
    ) {
        itemDao.insertItem(item)
    }

    // Update item
    suspend fun updateItem(
        item: ItemEntity
    ): Int {
        return itemDao.updateItem(item)
    }

    // Get item by ID
    suspend fun getItemById(
        itemId: Int
    ): ItemEntity? {
        return itemDao.getItemById(itemId)
    }

    // Get all items
    suspend fun getAllItems(): List<ItemEntity> {
        return itemDao.getAllItems()
    }

    // Get LOST items
    suspend fun getLostItems(): List<ItemEntity> {
        return itemDao.getLostItems()
    }

    // Get FOUND items
    suspend fun getFoundItems(): List<ItemEntity> {
        return itemDao.getFoundItems()
    }

    // Search items
    suspend fun searchItems(
        query: String
    ): List<ItemEntity> {
        return itemDao.searchItems(query)
    }

    // Get items by category
    suspend fun getItemsByCategory(
        category: String
    ): List<ItemEntity> {
        return itemDao.getItemsByCategory(category)
    }

    // Get items by type and category
    suspend fun getItemsByTypeAndCategory(
        type: String,
        category: String
    ): List<ItemEntity> {
        return itemDao.getItemsByTypeAndCategory(
            type,
            category
        )
    }

    // Get owner's item
    suspend fun getItemByOwner(
        itemId: Int,
        userId: Int
    ): ItemEntity? {
        return itemDao.getItemByOwner(
            itemId,
            userId
        )
    }

    // Update owner's item
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
            itemId = itemId,
            userId = userId,
            name = name,
            description = description,
            category = category,
            location = location,
            date = date
        )
    }

    // Delete owner's item
    suspend fun deleteItemByOwner(
        itemId: Int,
        userId: Int
    ): Int {

        return itemDao.deleteItemByOwner(
            itemId,
            userId
        )
    }

    // Get current user's items
    suspend fun getItemsByUser(
        userId: Int
    ): List<ItemEntity> {

        return itemDao.getItemsByUser(
            userId
        )
    }
}