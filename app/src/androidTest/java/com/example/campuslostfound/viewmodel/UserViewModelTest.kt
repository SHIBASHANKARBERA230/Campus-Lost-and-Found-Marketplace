package com.example.campuslostfound.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.campuslostfound.database.PasswordHasher
import com.example.campuslostfound.database.UserDao
import com.example.campuslostfound.database.UserEntity
import com.example.campuslostfound.database.UserRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class UserViewModelTest {

    private lateinit var dao: FakeUserDao
    private lateinit var repository: UserRepository
    private lateinit var viewModel: UserViewModel

    @Before
    fun setup() {

        dao = FakeUserDao()

        repository = UserRepository(dao)

        viewModel = UserViewModel(repository)
    }

    // =========================================================
    // TEST 1
    // REGISTER NEW USER
    // =========================================================

    @Test
    fun register_newUser_returnsSuccess() {

        val latch = CountDownLatch(1)

        var result: Boolean? = null
        var message: String? = null

        viewModel.register(
            name = "Test User",
            email = "test@example.com",
            phone = "9876543210",
            password = "password123"
        ) { success, resultMessage ->

            result = success
            message = resultMessage

            latch.countDown()
        }

        assertEquals(
            true,
            latch.await(5, TimeUnit.SECONDS)
        )

        assertEquals(
            true,
            result
        )

        assertEquals(
            "Registration successful ✅",
            message
        )

        assertEquals(
            1,
            dao.users.size
        )
    }

    // =========================================================
    // TEST 2
    // REGISTER EXISTING EMAIL
    // =========================================================

    @Test
    fun register_existingEmail_returnsFailure() {

        val salt = PasswordHasher.generateSalt()

        val existingUser = UserEntity(
            id = 1,
            name = "Existing User",
            email = "existing@example.com",
            phone = "9876543210",
            passwordHash = PasswordHasher.hashPassword(
                password = "password123",
                saltString = salt
            ),
            passwordSalt = salt
        )

        dao.users.add(existingUser)

        val latch = CountDownLatch(1)

        var result: Boolean? = null
        var message: String? = null

        viewModel.register(
            name = "New User",
            email = "existing@example.com",
            phone = "9999999999",
            password = "newPassword"
        ) { success, resultMessage ->

            result = success
            message = resultMessage

            latch.countDown()
        }

        assertEquals(
            true,
            latch.await(5, TimeUnit.SECONDS)
        )

        assertEquals(
            false,
            result
        )

        assertEquals(
            "Email already registered ❌",
            message
        )

        assertEquals(
            1,
            dao.users.size
        )
    }

    // =========================================================
    // TEST 3
    // LOGIN CORRECT PASSWORD
    // =========================================================

    @Test
    fun login_correctPassword_returnsSuccess() {

        val salt = PasswordHasher.generateSalt()

        val user = UserEntity(
            id = 1,
            name = "Test User",
            email = "test@example.com",
            phone = "9876543210",
            passwordHash = PasswordHasher.hashPassword(
                password = "password123",
                saltString = salt
            ),
            passwordSalt = salt
        )

        dao.users.add(user)

        val latch = CountDownLatch(1)

        var result: Boolean? = null
        var loggedInUser: UserEntity? = null

        viewModel.login(
            email = "test@example.com",
            password = "password123"
        ) { success, returnedUser ->

            result = success
            loggedInUser = returnedUser

            latch.countDown()
        }

        assertEquals(
            true,
            latch.await(5, TimeUnit.SECONDS)
        )

        assertEquals(
            true,
            result
        )

        assertNotNull(
            loggedInUser
        )

        assertEquals(
            user.id,
            loggedInUser?.id
        )

        assertEquals(
            user.email,
            loggedInUser?.email
        )
    }

    // =========================================================
    // TEST 4
    // LOGIN WRONG PASSWORD
    // =========================================================

    @Test
    fun login_wrongPassword_returnsFailure() {

        val salt = PasswordHasher.generateSalt()

        val user = UserEntity(
            id = 1,
            name = "Test User",
            email = "test@example.com",
            phone = "9876543210",
            passwordHash = PasswordHasher.hashPassword(
                password = "password123",
                saltString = salt
            ),
            passwordSalt = salt
        )

        dao.users.add(user)

        val latch = CountDownLatch(1)

        var result: Boolean? = null
        var loggedInUser: UserEntity? = null

        viewModel.login(
            email = "test@example.com",
            password = "wrongPassword"
        ) { success, returnedUser ->

            result = success
            loggedInUser = returnedUser

            latch.countDown()
        }

        assertEquals(
            true,
            latch.await(5, TimeUnit.SECONDS)
        )

        assertEquals(
            false,
            result
        )

        assertNull(
            loggedInUser
        )
    }

    // =========================================================
    // TEST 5
    // LOGIN UNKNOWN EMAIL
    // =========================================================

    @Test
    fun login_unknownEmail_returnsFailure() {

        val latch = CountDownLatch(1)

        var result: Boolean? = null
        var loggedInUser: UserEntity? = null

        viewModel.login(
            email = "unknown@example.com",
            password = "password123"
        ) { success, returnedUser ->

            result = success
            loggedInUser = returnedUser

            latch.countDown()
        }

        assertEquals(
            true,
            latch.await(5, TimeUnit.SECONDS)
        )

        assertEquals(
            false,
            result
        )

        assertNull(
            loggedInUser
        )
    }

    // =========================================================
    // FAKE USER DAO
    // =========================================================

    private class FakeUserDao : UserDao {

        val users = mutableListOf<UserEntity>()

        override suspend fun insertUser(
            user: UserEntity
        ): Long {

            val id = users.size + 1

            users.add(
                user.copy(id = id)
            )

            return id.toLong()
        }

        override suspend fun getUserByEmail(
            email: String
        ): UserEntity? {

            return users.find {
                it.email == email
            }
        }

        override suspend fun emailExists(
            email: String
        ): Int {

            return if (
                users.any {
                    it.email == email
                }
            ) {
                1
            } else {
                0
            }
        }

        override suspend fun getUserById(
            userId: Int
        ): UserEntity? {

            return users.find {
                it.id == userId
            }
        }

        override suspend fun updateUserProfile(
            userId: Int,
            name: String,
            phone: String
        ) {

            val index = users.indexOfFirst {
                it.id == userId
            }

            if (index != -1) {

                val oldUser = users[index]

                users[index] = oldUser.copy(
                    name = name,
                    phone = phone
                )
            }
        }

        override suspend fun updatePassword(
            userId: Int,
            passwordHash: String,
            passwordSalt: String
        ) {

            val index = users.indexOfFirst {
                it.id == userId
            }

            if (index != -1) {

                val oldUser = users[index]

                users[index] = oldUser.copy(
                    passwordHash = passwordHash,
                    passwordSalt = passwordSalt
                )
            }
        }

        override suspend fun getAdminById(
            userId: Int
        ): UserEntity? {

            return users.find {
                it.id == userId && it.isAdmin
            }
        }

        override suspend fun getAllUsers(): List<UserEntity> {

            return users.toList()
        }

        override suspend fun activateUser(
            userId: Int
        ): Int {

            val index = users.indexOfFirst {
                it.id == userId
            }

            if (index != -1) {

                val oldUser = users[index]

                users[index] = oldUser.copy(
                    isActive = true
                )

                return 1
            }

            return 0
        }

        override suspend fun deactivateUser(
            userId: Int
        ): Int {

            val index = users.indexOfFirst {
                it.id == userId
            }

            if (index != -1) {

                val oldUser = users[index]

                users[index] = oldUser.copy(
                    isActive = false
                )

                return 1
            }

            return 0
        }

        override suspend fun makeUserAdmin(
            userId: Int
        ): Int {

            val index = users.indexOfFirst {
                it.id == userId
            }

            if (index != -1) {

                val oldUser = users[index]

                users[index] = oldUser.copy(
                    isAdmin = true
                )

                return 1
            }

            return 0
        }

        override suspend fun removeAdmin(
            userId: Int
        ): Int {

            val index = users.indexOfFirst {
                it.id == userId
            }

            if (index != -1) {

                val oldUser = users[index]

                users[index] = oldUser.copy(
                    isAdmin = false
                )

                return 1
            }

            return 0
        }

        override suspend fun getTotalUsers(): Int {

            return users.size
        }

        override suspend fun getActiveUsers(): Int {

            return users.count {
                it.isActive
            }
        }

        override suspend fun getInactiveUsers(): Int {

            return users.count {
                !it.isActive
            }
        }

        override suspend fun getAdminUsers(): Int {

            return users.count {
                it.isAdmin
            }
        }
    }
}