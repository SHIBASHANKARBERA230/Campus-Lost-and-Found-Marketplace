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
class ItemRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: ItemRepository

    @Before
    fun setup() {
        val context =
            ApplicationProvider.getApplicationContext<Context>()

        database =
            Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()

        repository =
            ItemRepository(
                database.itemDao()
            )
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createItem(
        userId: Int = 1,
        name: String = "Wallet",
        description: String = "Black leather wallet",
        category: String = "Wallet",
        type: String = "LOST",
        location: String = "Library",
        date: String = "2026-09-26",
        status: String = "ACTIVE"
    ): ItemEntity {
        return ItemEntity(
            userId = userId,
            name = name,
            description = description,
            category = category,
            type = type,
            location = location,
            date = date,
            status = status
        )
    }

    @Test
    fun insertItem_insertsSuccessfully() = runBlocking {

        val id =
            repository.insertItem(
                createItem()
            )

        assertTrue(id > 0)

        val item =
            repository.getItemById(id.toInt())

        assertNotNull(item)
        assertEquals("Wallet", item!!.name)
        assertEquals(1, item.userId)
    }

    @Test
    fun updateItem_updatesSuccessfully() = runBlocking {

        val id =
            repository.insertItem(
                createItem()
            )

        val original =
            repository.getItemById(id.toInt())!!

        val updated =
            original.copy(
                name = "Updated Wallet",
                description = "Updated description"
            )

        val rowsUpdated =
            repository.updateItem(updated)

        assertEquals(1, rowsUpdated)

        val result =
            repository.getItemById(id.toInt())

        assertEquals("Updated Wallet", result!!.name)
        assertEquals("Updated description", result.description)
    }

    @Test
    fun getItemById_returnsCorrectItem() = runBlocking {

        val id =
            repository.insertItem(
                createItem(
                    name = "Mobile Phone"
                )
            )

        val item =
            repository.getItemById(id.toInt())

        assertNotNull(item)
        assertEquals("Mobile Phone", item!!.name)
    }

    @Test
    fun getItemById_returnsNullForUnknownItem() = runBlocking {

        val item =
            repository.getItemById(9999)

        assertNull(item)
    }

    @Test
    fun getAllItems_returnsAllItems() = runBlocking {

        repository.insertItem(
            createItem(
                name = "Wallet"
            )
        )

        repository.insertItem(
            createItem(
                name = "Mobile"
            )
        )

        repository.insertItem(
            createItem(
                name = "Bag"
            )
        )

        val items =
            repository.getAllItems()

        assertEquals(3, items.size)
    }

    @Test
    fun getLostItems_returnsOnlyLostItems() = runBlocking {

        repository.insertItem(
            createItem(
                name = "Wallet",
                type = "LOST"
            )
        )

        repository.insertItem(
            createItem(
                name = "Phone",
                type = "FOUND"
            )
        )

        repository.insertItem(
            createItem(
                name = "Bag",
                type = "LOST"
            )
        )

        val items =
            repository.getLostItems()

        assertEquals(2, items.size)

        assertTrue(
            items.all {
                it.type == "LOST"
            }
        )
    }

    @Test
    fun getFoundItems_returnsOnlyFoundItems() = runBlocking {

        repository.insertItem(
            createItem(
                name = "Wallet",
                type = "LOST"
            )
        )

        repository.insertItem(
            createItem(
                name = "Phone",
                type = "FOUND"
            )
        )

        repository.insertItem(
            createItem(
                name = "Bag",
                type = "FOUND"
            )
        )

        val items =
            repository.getFoundItems()

        assertEquals(2, items.size)

        assertTrue(
            items.all {
                it.type == "FOUND"
            }
        )
    }

    @Test
    fun searchItems_returnsMatchingItems() = runBlocking {

        repository.insertItem(
            createItem(
                name = "Black Wallet",
                description = "Leather wallet",
                category = "Wallet"
            )
        )

        repository.insertItem(
            createItem(
                name = "Blue Backpack",
                description = "College backpack",
                category = "Bag"
            )
        )

        val results =
            repository.searchItems("Wallet")

        assertEquals(1, results.size)
        assertEquals("Black Wallet", results[0].name)
    }

    @Test
    fun getItemsByCategory_returnsMatchingCategory() = runBlocking {

        repository.insertItem(
            createItem(
                name = "Wallet",
                category = "Wallet"
            )
        )

        repository.insertItem(
            createItem(
                name = "Phone",
                category = "Electronics"
            )
        )

        repository.insertItem(
            createItem(
                name = "Purse",
                category = "Wallet"
            )
        )

        val items =
            repository.getItemsByCategory("Wallet")

        assertEquals(2, items.size)

        assertTrue(
            items.all {
                it.category == "Wallet"
            }
        )
    }

    @Test
    fun getItemsByTypeAndCategory_returnsMatchingItems() =
        runBlocking {

            repository.insertItem(
                createItem(
                    name = "Lost Wallet",
                    type = "LOST",
                    category = "Wallet"
                )
            )

            repository.insertItem(
                createItem(
                    name = "Found Wallet",
                    type = "FOUND",
                    category = "Wallet"
                )
            )

            repository.insertItem(
                createItem(
                    name = "Lost Phone",
                    type = "LOST",
                    category = "Electronics"
                )
            )

            val items =
                repository.getItemsByTypeAndCategory(
                    type = "LOST",
                    category = "Wallet"
                )

            assertEquals(1, items.size)
            assertEquals("Lost Wallet", items[0].name)
        }

    @Test
    fun getItemByOwner_returnsItemForCorrectOwner() =
        runBlocking {

            val id =
                repository.insertItem(
                    createItem(
                        userId = 10,
                        name = "Owner Item"
                    )
                )

            val item =
                repository.getItemByOwner(
                    itemId = id.toInt(),
                    userId = 10
                )

            assertNotNull(item)
            assertEquals(10, item!!.userId)
        }

    @Test
    fun getItemByOwner_returnsNullForWrongOwner() =
        runBlocking {

            val id =
                repository.insertItem(
                    createItem(
                        userId = 10,
                        name = "Owner Item"
                    )
                )

            val item =
                repository.getItemByOwner(
                    itemId = id.toInt(),
                    userId = 20
                )

            assertNull(item)
        }

    @Test
    fun deleteItemByOwner_deletesOnlyCorrectOwnerItem() =
        runBlocking {

            val ownerItemId =
                repository.insertItem(
                    createItem(
                        userId = 10,
                        name = "Owner Item"
                    )
                )

            val otherItemId =
                repository.insertItem(
                    createItem(
                        userId = 20,
                        name = "Other Item"
                    )
                )

            val rowsDeleted =
                repository.deleteItemByOwner(
                    itemId = ownerItemId.toInt(),
                    userId = 10
                )

            assertEquals(1, rowsDeleted)

            assertNull(
                repository.getItemById(
                    ownerItemId.toInt()
                )
            )

            assertNotNull(
                repository.getItemById(
                    otherItemId.toInt()
                )
            )
        }

    @Test
    fun updateItemByOwner_updatesCorrectOwnerItem() =
        runBlocking {

            val id =
                repository.insertItem(
                    createItem(
                        userId = 10,
                        name = "Old Name"
                    )
                )

            val rowsUpdated =
                repository.updateItemByOwner(
                    itemId = id.toInt(),
                    userId = 10,
                    name = "New Name",
                    description = "New Description",
                    category = "Bag",
                    location = "Hostel",
                    date = "2026-09-27"
                )

            assertEquals(1, rowsUpdated)

            val item =
                repository.getItemById(id.toInt())

            assertEquals("New Name", item!!.name)
            assertEquals("New Description", item.description)
            assertEquals("Bag", item.category)
            assertEquals("Hostel", item.location)
            assertEquals("2026-09-27", item.date)
        }

    @Test
    fun updateItemStatusByOwner_updatesStatus() =
        runBlocking {

            val id =
                repository.insertItem(
                    createItem(
                        userId = 10,
                        status = "ACTIVE"
                    )
                )

            val rowsUpdated =
                repository.updateItemStatusByOwner(
                    itemId = id.toInt(),
                    userId = 10,
                    status = "RESOLVED"
                )

            assertEquals(1, rowsUpdated)

            val item =
                repository.getItemById(id.toInt())

            assertEquals(
                "RESOLVED",
                item!!.status
            )
        }

    @Test
    fun getItemsByUser_returnsOnlyUserItems() = runBlocking {

        repository.insertItem(
            createItem(
                userId = 1,
                name = "User One Item"
            )
        )

        repository.insertItem(
            createItem(
                userId = 1,
                name = "User One Second Item"
            )
        )

        repository.insertItem(
            createItem(
                userId = 2,
                name = "User Two Item"
            )
        )

        val items =
            repository.getItemsByUser(1)

        assertEquals(2, items.size)

        assertTrue(
            items.all {
                it.userId == 1
            }
        )
    }

    @Test
    fun findMatchingItems_returnsMatchingOppositeTypeCategoryAndName() =
        runBlocking {

            repository.insertItem(
                createItem(
                    userId = 2,
                    name = "Black Wallet",
                    category = "Wallet",
                    type = "FOUND"
                )
            )

            repository.insertItem(
                createItem(
                    userId = 3,
                    name = "Black Phone",
                    category = "Electronics",
                    type = "FOUND"
                )
            )

            repository.insertItem(
                createItem(
                    userId = 4,
                    name = "Black Wallet",
                    category = "Wallet",
                    type = "FOUND"
                )
            )

            val results =
                repository.findMatchingItems(
                    oppositeType = "FOUND",
                    category = "Wallet",
                    name = "Black Wallet",
                    userId = 1
                )

            assertEquals(2, results.size)

            assertTrue(
                results.all {
                    it.type == "FOUND" &&
                            it.category == "Wallet" &&
                            it.name.contains(
                                "Black Wallet",
                                ignoreCase = true
                            ) &&
                            it.userId != 1
                }
            )
        }
}