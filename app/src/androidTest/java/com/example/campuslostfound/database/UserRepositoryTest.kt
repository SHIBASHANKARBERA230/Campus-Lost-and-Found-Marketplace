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
class UserRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: UserRepository

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
            UserRepository(
                database.userDao()
            )
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createUser(
        name: String = "Test User",
        email: String = "test@example.com",
        phone: String = "9876543210",
        passwordHash: String = "hash123",
        passwordSalt: String = "salt123",
        isAdmin: Boolean = false,
        isActive: Boolean = true
    ): UserEntity {
        return UserEntity(
            name = name,
            email = email,
            phone = phone,
            passwordHash = passwordHash,
            passwordSalt = passwordSalt,
            isAdmin = isAdmin,
            isActive = isActive
        )
    }

    @Test
    fun registerUser_registersSuccessfully() = runBlocking {

        val id =
            repository.registerUser(
                createUser()
            )

        assertTrue(id > 0)

        val user =
            repository.getUserById(id.toInt())

        assertNotNull(user)
        assertEquals("Test User", user!!.name)
        assertEquals("test@example.com", user.email)
    }

    @Test
    fun loginUser_returnsUserByEmail() = runBlocking {

        repository.registerUser(
            createUser(
                name = "Shiba",
                email = "shiba@example.com"
            )
        )

        val user =
            repository.loginUser(
                "shiba@example.com"
            )

        assertNotNull(user)
        assertEquals("Shiba", user!!.name)
        assertEquals("shiba@example.com", user.email)
    }

    @Test
    fun loginUser_returnsNullForUnknownEmail() = runBlocking {

        val user =
            repository.loginUser(
                "unknown@example.com"
            )

        assertNull(user)
    }

    @Test
    fun emailExists_returnsCorrectResult() = runBlocking {

        repository.registerUser(
            createUser(
                email = "existing@example.com"
            )
        )

        assertTrue(
            repository.emailExists(
                "existing@example.com"
            )
        )

        assertFalse(
            repository.emailExists(
                "unknown@example.com"
            )
        )
    }

    @Test
    fun getUserById_returnsCorrectUser() = runBlocking {

        val id =
            repository.registerUser(
                createUser(
                    name = "Student",
                    email = "student@example.com"
                )
            )

        val user =
            repository.getUserById(id.toInt())

        assertNotNull(user)
        assertEquals("Student", user!!.name)
        assertEquals("student@example.com", user.email)
    }

    @Test
    fun getUserById_returnsNullForUnknownUser() = runBlocking {

        val user =
            repository.getUserById(9999)

        assertNull(user)
    }

    @Test
    fun updateUserProfile_updatesNameAndPhone() = runBlocking {

        val id =
            repository.registerUser(
                createUser(
                    name = "Old Name",
                    phone = "1111111111"
                )
            )

        repository.updateUserProfile(
            userId = id.toInt(),
            name = "New Name",
            phone = "9999999999"
        )

        val user =
            repository.getUserById(id.toInt())

        assertNotNull(user)
        assertEquals("New Name", user!!.name)
        assertEquals("9999999999", user.phone)
    }

    @Test
    fun updatePassword_updatesPasswordHashAndSalt() = runBlocking {

        val id =
            repository.registerUser(
                createUser(
                    passwordHash = "oldHash",
                    passwordSalt = "oldSalt"
                )
            )

        repository.updatePassword(
            userId = id.toInt(),
            passwordHash = "newHash",
            passwordSalt = "newSalt"
        )

        val user =
            repository.getUserById(id.toInt())

        assertNotNull(user)
        assertEquals("newHash", user!!.passwordHash)
        assertEquals("newSalt", user.passwordSalt)
    }
}