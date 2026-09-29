package com.example.campuslostfound.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ItemDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var itemDao: ItemDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        itemDao = database.itemDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    private fun createItem(
        userId: Int = 1,
        name: String = "Black Wallet",
        description: String = "Black leather wallet",
        category: String = "Wallet",
        type: String = "LOST",
        location: String = "Library",
        date: String = "2026-09-26",
        status: String = "OPEN",
        imageUri: String? = null
    ): ItemEntity {
        return ItemEntity(
            userId = userId,
            name = name,
            description = description,
            category = category,
            type = type,
            location = location,
            date = date,
            status = status,
            imageUri = imageUri
        )
    }

    // =========================================
    // INSERT ITEM
    // =========================================

    @Test
    fun insertItem_returnsGeneratedId() = runBlocking {

        val item = createItem()

        val id = itemDao.insertItem(item)

        assertTrue(id > 0)
    }

    // =========================================
    // UPDATE ITEM
    // =========================================

    @Test
    fun updateItem_updatesExistingItem() = runBlocking {

        val item = createItem()

        val id = itemDao.insertItem(item)

        val insertedItem = itemDao.getItemById(id.toInt())

        assertNotNull(insertedItem)

        val updatedItem = insertedItem!!.copy(
            name = "Updated Wallet",
            description = "Updated description",
            location = "Hostel",
            status = "RESOLVED"
        )

        val rowsUpdated = itemDao.updateItem(updatedItem)

        assertEquals(1, rowsUpdated)

        val result = itemDao.getItemById(id.toInt())

        assertNotNull(result)
        assertEquals("Updated Wallet", result!!.name)
        assertEquals("Updated description", result.description)
        assertEquals("Hostel", result.location)
        assertEquals("RESOLVED", result.status)
    }

    // =========================================
    // GET ITEM BY ID
    // =========================================

    @Test
    fun getItemById_returnsCorrectItem() = runBlocking {

        val id = itemDao.insertItem(
            createItem(
                name = "Blue Bag"
            )
        )

        val result = itemDao.getItemById(id.toInt())

        assertNotNull(result)
        assertEquals("Blue Bag", result!!.name)
    }

    @Test
    fun getItemById_unknownId_returnsNull() = runBlocking {

        val result = itemDao.getItemById(9999)

        assertNull(result)
    }

    // =========================================
    // GET ALL ITEMS
    // =========================================

    @Test
    fun getAllItems_returnsItemsInDescendingIdOrder() = runBlocking {

        val firstId = itemDao.insertItem(
            createItem(
                name = "First Item"
            )
        )

        val secondId = itemDao.insertItem(
            createItem(
                name = "Second Item"
            )
        )

        val items = itemDao.getAllItems()

        assertEquals(2, items.size)
        assertEquals(secondId.toInt(), items[0].id)
        assertEquals(firstId.toInt(), items[1].id)
    }

    // =========================================
    // GET LOST ITEMS
    // =========================================

    @Test
    fun getLostItems_returnsOnlyLostItems() = runBlocking {

        itemDao.insertItem(
            createItem(
                name = "Lost Phone",
                type = "LOST"
            )
        )

        itemDao.insertItem(
            createItem(
                name = "Found Watch",
                type = "FOUND"
            )
        )

        val items = itemDao.getLostItems()

        assertEquals(1, items.size)
        assertEquals("Lost Phone", items[0].name)
        assertEquals("LOST", items[0].type)
    }

    // =========================================
    // GET FOUND ITEMS
    // =========================================

    @Test
    fun getFoundItems_returnsOnlyFoundItems() = runBlocking {

        itemDao.insertItem(
            createItem(
                name = "Lost Phone",
                type = "LOST"
            )
        )

        itemDao.insertItem(
            createItem(
                name = "Found Watch",
                type = "FOUND"
            )
        )

        val items = itemDao.getFoundItems()

        assertEquals(1, items.size)
        assertEquals("Found Watch", items[0].name)
        assertEquals("FOUND", items[0].type)
    }

    // =========================================
    // SEARCH ITEMS
    // =========================================

    @Test
    fun searchItems_findsMatchingName() = runBlocking {

        itemDao.insertItem(
            createItem(
                name = "Black Wallet",
                description = "Leather item",
                category = "Accessories"
            )
        )

        itemDao.insertItem(
            createItem(
                name = "Blue Bag",
                description = "School bag",
                category = "Bags"
            )
        )

        val items = itemDao.searchItems("Wallet")

        assertEquals(1, items.size)
        assertEquals("Black Wallet", items[0].name)
    }

    @Test
    fun searchItems_findsMatchingDescription() = runBlocking {

        itemDao.insertItem(
            createItem(
                name = "Black Wallet",
                description = "Leather wallet"
            )
        )

        val items = itemDao.searchItems("Leather")

        assertEquals(1, items.size)
        assertEquals("Black Wallet", items[0].name)
    }

    @Test
    fun searchItems_findsMatchingCategory() = runBlocking {

        itemDao.insertItem(
            createItem(
                name = "Black Wallet",
                category = "Accessories"
            )
        )

        val items = itemDao.searchItems("Accessories")

        assertEquals(1, items.size)
        assertEquals("Accessories", items[0].category)
    }

    @Test
    fun searchItems_findsMatchingLocation() = runBlocking {

        itemDao.insertItem(
            createItem(
                name = "Black Wallet",
                location = "Library"
            )
        )

        val items = itemDao.searchItems("Library")

        assertEquals(1, items.size)
        assertEquals("Library", items[0].location)
    }

    @Test
    fun searchItems_noMatch_returnsEmptyList() = runBlocking {

        itemDao.insertItem(
            createItem(
                name = "Black Wallet"
            )
        )

        val items = itemDao.searchItems("Laptop")

        assertTrue(items.isEmpty())
    }

    // =========================================
    // GET ITEMS BY CATEGORY
    // =========================================

    @Test
    fun getItemsByCategory_returnsMatchingCategory() = runBlocking {

        itemDao.insertItem(
            createItem(
                name = "Phone",
                category = "Electronics"
            )
        )

        itemDao.insertItem(
            createItem(
                name = "Wallet",
                category = "Accessories"
            )
        )

        val items = itemDao.getItemsByCategory("Electronics")

        assertEquals(1, items.size)
        assertEquals("Phone", items[0].name)
        assertEquals("Electronics", items[0].category)
    }

    // =========================================
    // GET ITEMS BY TYPE AND CATEGORY
    // =========================================

    @Test
    fun getItemsByTypeAndCategory_returnsMatchingItems() = runBlocking {

        itemDao.insertItem(
            createItem(
                name = "Lost Phone",
                type = "LOST",
                category = "Electronics"
            )
        )

        itemDao.insertItem(
            createItem(
                name = "Found Phone",
                type = "FOUND",
                category = "Electronics"
            )
        )

        itemDao.insertItem(
            createItem(
                name = "Lost Wallet",
                type = "LOST",
                category = "Accessories"
            )
        )

        val items = itemDao.getItemsByTypeAndCategory(
            type = "LOST",
            category = "Electronics"
        )

        assertEquals(1, items.size)
        assertEquals("Lost Phone", items[0].name)
    }

    // =========================================
    // GET ITEM BY OWNER
    // =========================================

    @Test
    fun getItemByOwner_correctOwner_returnsItem() = runBlocking {

        val id = itemDao.insertItem(
            createItem(
                userId = 10,
                name = "Owner Wallet"
            )
        )

        val result = itemDao.getItemByOwner(
            itemId = id.toInt(),
            userId = 10
        )

        assertNotNull(result)
        assertEquals("Owner Wallet", result!!.name)
        assertEquals(10, result.userId)
    }

    @Test
    fun getItemByOwner_wrongOwner_returnsNull() = runBlocking {

        val id = itemDao.insertItem(
            createItem(
                userId = 10,
                name = "Owner Wallet"
            )
        )

        val result = itemDao.getItemByOwner(
            itemId = id.toInt(),
            userId = 20
        )

        assertNull(result)
    }

    // =========================================
    // DELETE ITEM BY OWNER
    // =========================================

    @Test
    fun deleteItemByOwner_correctOwner_deletesItem() = runBlocking {

        val id = itemDao.insertItem(
            createItem(
                userId = 10,
                name = "Delete Wallet"
            )
        )

        val deletedRows = itemDao.deleteItemByOwner(
            itemId = id.toInt(),
            userId = 10
        )

        assertEquals(1, deletedRows)
        assertNull(itemDao.getItemById(id.toInt()))
    }

    @Test
    fun deleteItemByOwner_wrongOwner_doesNotDeleteItem() = runBlocking {

        val id = itemDao.insertItem(
            createItem(
                userId = 10,
                name = "Protected Wallet"
            )
        )

        val deletedRows = itemDao.deleteItemByOwner(
            itemId = id.toInt(),
            userId = 20
        )

        assertEquals(0, deletedRows)
        assertNotNull(itemDao.getItemById(id.toInt()))
    }

    // =========================================
    // DELETE ITEM BY ID
    // =========================================

    @Test
    fun deleteItemById_deletesItem() = runBlocking {

        val id = itemDao.insertItem(
            createItem(
                name = "Admin Delete Item"
            )
        )

        val deletedRows = itemDao.deleteItemById(
            id.toInt()
        )

        assertEquals(1, deletedRows)
        assertNull(itemDao.getItemById(id.toInt()))
    }

    @Test
    fun deleteItemById_unknownId_returnsZero() = runBlocking {

        val deletedRows = itemDao.deleteItemById(9999)

        assertEquals(0, deletedRows)
    }

    // =========================================
    // UPDATE ITEM BY OWNER
    // =========================================

    @Test
    fun updateItemByOwner_correctOwner_updatesItem() = runBlocking {

        val id = itemDao.insertItem(
            createItem(
                userId = 10,
                name = "Old Name"
            )
        )

        val rowsUpdated = itemDao.updateItemByOwner(
            itemId = id.toInt(),
            userId = 10,
            name = "New Name",
            description = "New Description",
            category = "Electronics",
            location = "New Location",
            date = "2026-09-27"
        )

        assertEquals(1, rowsUpdated)

        val result = itemDao.getItemById(id.toInt())

        assertNotNull(result)
        assertEquals("New Name", result!!.name)
        assertEquals("New Description", result.description)
        assertEquals("Electronics", result.category)
        assertEquals("New Location", result.location)
        assertEquals("2026-09-27", result.date)
    }

    @Test
    fun updateItemByOwner_wrongOwner_doesNotUpdateItem() = runBlocking {

        val id = itemDao.insertItem(
            createItem(
                userId = 10,
                name = "Original Name"
            )
        )

        val rowsUpdated = itemDao.updateItemByOwner(
            itemId = id.toInt(),
            userId = 20,
            name = "Changed Name",
            description = "Changed Description",
            category = "Electronics",
            location = "Changed Location",
            date = "2026-09-27"
        )

        assertEquals(0, rowsUpdated)

        val result = itemDao.getItemById(id.toInt())

        assertNotNull(result)
        assertEquals("Original Name", result!!.name)
    }

    // =========================================
    // UPDATE ITEM STATUS BY OWNER
    // =========================================

    @Test
    fun updateItemStatusByOwner_correctOwner_updatesStatus() = runBlocking {

        val id = itemDao.insertItem(
            createItem(
                userId = 10,
                status = "OPEN"
            )
        )

        val rowsUpdated = itemDao.updateItemStatusByOwner(
            itemId = id.toInt(),
            userId = 10,
            status = "RESOLVED"
        )

        assertEquals(1, rowsUpdated)

        val result = itemDao.getItemById(id.toInt())

        assertNotNull(result)
        assertEquals("RESOLVED", result!!.status)
    }

    @Test
    fun updateItemStatusByOwner_wrongOwner_doesNotUpdateStatus() = runBlocking {

        val id = itemDao.insertItem(
            createItem(
                userId = 10,
                status = "OPEN"
            )
        )

        val rowsUpdated = itemDao.updateItemStatusByOwner(
            itemId = id.toInt(),
            userId = 20,
            status = "RESOLVED"
        )

        assertEquals(0, rowsUpdated)

        val result = itemDao.getItemById(id.toInt())

        assertNotNull(result)
        assertEquals("OPEN", result!!.status)
    }

    // =========================================
    // GET ITEMS BY USER
    // =========================================

    @Test
    fun getItemsByUser_returnsOnlyUsersItems() = runBlocking {

        itemDao.insertItem(
            createItem(
                userId = 10,
                name = "User 10 Item"
            )
        )

        itemDao.insertItem(
            createItem(
                userId = 20,
                name = "User 20 Item"
            )
        )

        itemDao.insertItem(
            createItem(
                userId = 10,
                name = "User 10 Second Item"
            )
        )

        val items = itemDao.getItemsByUser(10)

        assertEquals(2, items.size)
        assertEquals("User 10 Second Item", items[0].name)
        assertEquals("User 10 Item", items[1].name)
    }

    // =========================================
    // FIND POSSIBLE LOST / FOUND MATCHES
    // =========================================

    @Test
    fun findMatchingItems_returnsOppositeTypeSameCategoryAndMatchingName() =
        runBlocking {

            itemDao.insertItem(
                createItem(
                    userId = 10,
                    name = "Black Wallet",
                    category = "Wallet",
                    type = "LOST"
                )
            )

            itemDao.insertItem(
                createItem(
                    userId = 20,
                    name = "Black Wallet Found",
                    category = "Wallet",
                    type = "FOUND"
                )
            )

            val matches = itemDao.findMatchingItems(
                oppositeType = "FOUND",
                category = "Wallet",
                name = "Black Wallet",
                userId = 10
            )

            assertEquals(1, matches.size)
            assertEquals("Black Wallet Found", matches[0].name)
        }

    @Test
    fun findMatchingItems_excludesSameUser() = runBlocking {

        itemDao.insertItem(
            createItem(
                userId = 10,
                name = "Black Wallet",
                category = "Wallet",
                type = "FOUND"
            )
        )

        val matches = itemDao.findMatchingItems(
            oppositeType = "FOUND",
            category = "Wallet",
            name = "Black Wallet",
            userId = 10
        )

        assertTrue(matches.isEmpty())
    }

    @Test
    fun findMatchingItems_requiresSameCategory() = runBlocking {

        itemDao.insertItem(
            createItem(
                userId = 20,
                name = "Black Wallet",
                category = "Accessories",
                type = "FOUND"
            )
        )

        val matches = itemDao.findMatchingItems(
            oppositeType = "FOUND",
            category = "Wallet",
            name = "Black Wallet",
            userId = 10
        )

        assertTrue(matches.isEmpty())
    }

    // =========================================
    // GET RECENT ITEMS
    // =========================================

    @Test
    fun getRecentItems_returnsMaximumFiveItems() = runBlocking {

        repeat(7) { index ->

            itemDao.insertItem(
                createItem(
                    name = "Item $index"
                )
            )
        }

        val items = itemDao.getRecentItems()

        assertEquals(5, items.size)
    }

    @Test
    fun getRecentItems_returnsNewestItemsFirst() = runBlocking {

        itemDao.insertItem(
            createItem(
                name = "Item 1"
            )
        )

        itemDao.insertItem(
            createItem(
                name = "Item 2"
            )
        )

        itemDao.insertItem(
            createItem(
                name = "Item 3"
            )
        )

        val items = itemDao.getRecentItems()

        assertEquals("Item 3", items[0].name)
        assertEquals("Item 2", items[1].name)
        assertEquals("Item 1", items[2].name)
    }

    // =========================================
    // ADMIN DASHBOARD COUNTS
    // =========================================

    @Test
    fun getTotalItems_returnsCorrectCount() = runBlocking {

        itemDao.insertItem(
            createItem()
        )

        itemDao.insertItem(
            createItem()
        )

        itemDao.insertItem(
            createItem()
        )

        assertEquals(3, itemDao.getTotalItems())
    }

    @Test
    fun getTotalLostItems_returnsCorrectCount() = runBlocking {

        itemDao.insertItem(
            createItem(
                type = "LOST"
            )
        )

        itemDao.insertItem(
            createItem(
                type = "LOST"
            )
        )

        itemDao.insertItem(
            createItem(
                type = "FOUND"
            )
        )

        assertEquals(2, itemDao.getTotalLostItems())
    }

    @Test
    fun getTotalFoundItems_returnsCorrectCount() = runBlocking {

        itemDao.insertItem(
            createItem(
                type = "FOUND"
            )
        )

        itemDao.insertItem(
            createItem(
                type = "FOUND"
            )
        )

        itemDao.insertItem(
            createItem(
                type = "LOST"
            )
        )

        assertEquals(2, itemDao.getTotalFoundItems())
    }

    // =========================================
    // IMAGE URI
    // =========================================

    @Test
    fun insertItem_preservesImageUri() = runBlocking {

        val imageUri =
            "content://media/external/images/123"

        val id = itemDao.insertItem(
            createItem(
                imageUri = imageUri
            )
        )

        val result = itemDao.getItemById(id.toInt())

        assertNotNull(result)
        assertEquals(imageUri, result!!.imageUri)
    }

    @Test
    fun insertItem_allowsNullImageUri() = runBlocking {

        val id = itemDao.insertItem(
            createItem(
                imageUri = null
            )
        )

        val result = itemDao.getItemById(id.toInt())

        assertNotNull(result)
        assertNull(result!!.imageUri)
    }
}