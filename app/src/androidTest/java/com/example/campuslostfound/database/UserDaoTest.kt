package com.example.campuslostfound.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

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

        userDao =
                database.userDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createUser(
            name: String = "Test User",
            email: String = "test@example.com",
            phone: String = "9876543210",
            isAdmin: Boolean = false,
            isActive: Boolean = true
    ): UserEntity {

        return UserEntity(
                name = name,
                email = email,
                phone = phone,
                passwordHash = "testHash",
                passwordSalt = "testSalt",
                isAdmin = isAdmin,
                isActive = isActive
        )
    }

    @Test
    fun insertUser_returnsGeneratedId() = runBlocking {

        val user =
                createUser()

        val userId =
                userDao.insertUser(user)

        assertTrue(userId > 0)
    }

    @Test
    fun getUserByEmail_returnsInsertedUser() = runBlocking {

        val user =
                createUser(
                        email = "shiba@example.com"
                )

        userDao.insertUser(user)

        val result =
                userDao.getUserByEmail(
                        "shiba@example.com"
                )

        assertNotNull(result)
        assertEquals(
                "Test User",
                result?.name
        )
        assertEquals(
                "shiba@example.com",
                result?.email
        )
    }

    @Test
    fun getUserByEmail_unknownEmail_returnsNull() = runBlocking {

        val result =
                userDao.getUserByEmail(
                        "unknown@example.com"
                )

        assertNull(result)
    }

    @Test
    fun emailExists_existingEmail_returnsOne() = runBlocking {

        val user =
                createUser(
                        email = "exists@example.com"
                )

        userDao.insertUser(user)

        val count =
                userDao.emailExists(
                        "exists@example.com"
                )

        assertEquals(
                1,
                count
        )
    }

    @Test
    fun emailExists_unknownEmail_returnsZero() = runBlocking {

        val count =
                userDao.emailExists(
                        "missing@example.com"
                )

        assertEquals(
                0,
                count
        )
    }

    @Test
    fun getUserById_returnsCorrectUser() = runBlocking {

        val userId =
                userDao.insertUser(
                        createUser(
                                name = "User By ID",
                                email = "userid@example.com"
                        )
                )

        val result =
                userDao.getUserById(
                        userId.toInt()
                )

        assertNotNull(result)
        assertEquals(
                "User By ID",
                result?.name
        )
        assertEquals(
                "userid@example.com",
                result?.email
        )
    }

    @Test
    fun updateUserProfile_updatesNameAndPhone() = runBlocking {

        val userId =
                userDao.insertUser(
                        createUser(
                                name = "Old Name",
                                phone = "1111111111"
                        )
                )

        userDao.updateUserProfile(
                userId = userId.toInt(),
                name = "New Name",
                phone = "9999999999"
        )

        val result =
                userDao.getUserById(
                        userId.toInt()
                )

        assertNotNull(result)
        assertEquals(
                "New Name",
                result?.name
        )
        assertEquals(
                "9999999999",
                result?.phone
        )
    }

    @Test
    fun updatePassword_updatesPasswordHashAndSalt() = runBlocking {

        val userId =
                userDao.insertUser(
                        createUser()
                )

        userDao.updatePassword(
                userId = userId.toInt(),
                passwordHash = "newHash",
                passwordSalt = "newSalt"
        )

        val result =
                userDao.getUserById(
                        userId.toInt()
                )

        assertNotNull(result)
        assertEquals(
                "newHash",
                result?.passwordHash
        )
        assertEquals(
                "newSalt",
                result?.passwordSalt
        )
    }

    @Test
    fun getAdminById_adminUser_returnsUser() = runBlocking {

        val userId =
                userDao.insertUser(
                        createUser(
                                isAdmin = true
                        )
                )

        val result =
                userDao.getAdminById(
                        userId.toInt()
                )

        assertNotNull(result)
        assertEquals(
                userId.toInt(),
                result?.id
        )
        assertTrue(
                result?.isAdmin == true
        )
    }

    @Test
    fun getAdminById_normalUser_returnsNull() = runBlocking {

        val userId =
                userDao.insertUser(
                        createUser(
                                isAdmin = false
                        )
                )

        val result =
                userDao.getAdminById(
                        userId.toInt()
                )

        assertNull(result)
    }

    @Test
    fun getAllUsers_returnsUsersInDescendingIdOrder() = runBlocking {

        val firstId =
                userDao.insertUser(
                        createUser(
                                name = "First User",
                                email = "first@example.com"
                        )
                )

        val secondId =
                userDao.insertUser(
                        createUser(
                                name = "Second User",
                                email = "second@example.com"
                        )
                )

        val users =
                userDao.getAllUsers()

        assertEquals(
                2,
                users.size
        )

        assertEquals(
                secondId.toInt(),
                users[0].id
        )

        assertEquals(
                firstId.toInt(),
                users[1].id
        )
    }

    @Test
    fun deactivateUser_setsUserInactive() = runBlocking {

        val userId =
                userDao.insertUser(
                        createUser(
                                isActive = true
                        )
                )

        val rowsUpdated =
                userDao.deactivateUser(
                        userId.toInt()
                )

        assertEquals(
                1,
                rowsUpdated
        )

        val result =
                userDao.getUserById(
                        userId.toInt()
                )

        assertNotNull(result)
        assertFalse(
                result?.isActive == true
        )
    }

    @Test
    fun activateUser_setsUserActive() = runBlocking {

        val userId =
                userDao.insertUser(
                        createUser(
                                isActive = false
                        )
                )

        val rowsUpdated =
                userDao.activateUser(
                        userId.toInt()
                )

        assertEquals(
                1,
                rowsUpdated
        )

        val result =
                userDao.getUserById(
                        userId.toInt()
                )

        assertNotNull(result)
        assertTrue(
                result?.isActive == true
        )
    }

    @Test
    fun makeUserAdmin_setsAdminTrue() = runBlocking {

        val userId =
                userDao.insertUser(
                        createUser(
                                isAdmin = false
                        )
                )

        val rowsUpdated =
                userDao.makeUserAdmin(
                        userId.toInt()
                )

        assertEquals(
                1,
                rowsUpdated
        )

        val result =
                userDao.getUserById(
                        userId.toInt()
                )

        assertNotNull(result)
        assertTrue(
                result?.isAdmin == true
        )
    }

    @Test
    fun removeAdmin_setsAdminFalse() = runBlocking {

        val userId =
                userDao.insertUser(
                        createUser(
                                isAdmin = true
                        )
                )

        val rowsUpdated =
                userDao.removeAdmin(
                        userId.toInt()
                )

        assertEquals(
                1,
                rowsUpdated
        )

        val result =
                userDao.getUserById(
                        userId.toInt()
                )

        assertNotNull(result)
        assertFalse(
                result?.isAdmin == true
        )
    }

    @Test
    fun dashboardCounts_returnCorrectValues() = runBlocking {

        userDao.insertUser(
                createUser(
                        email = "admin@example.com",
                        isAdmin = true,
                        isActive = true
                )
        )

        userDao.insertUser(
                createUser(
                        email = "active@example.com",
                        isAdmin = false,
                        isActive = true
                )
        )

        userDao.insertUser(
                createUser(
                        email = "inactive@example.com",
                        isAdmin = false,
                        isActive = false
                )
        )

        assertEquals(
                3,
                userDao.getTotalUsers()
        )

        assertEquals(
                2,
                userDao.getActiveUsers()
        )

        assertEquals(
                1,
                userDao.getInactiveUsers()
        )

        assertEquals(
                1,
                userDao.getAdminUsers()
        )
    }
}