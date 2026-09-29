package com.example.campuslostfound.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PasswordHasherTest {

    @Test
    fun hashPassword_producesHash() {
        val password = "TestPassword123"
        val salt = PasswordHasher.generateSalt()

        val hash =
            PasswordHasher.hashPassword(
                password,
                salt
            )

        assertNotNull(hash)
        assertTrue(hash.isNotEmpty())
    }

    @Test
    fun verifyPassword_correctPassword_returnsTrue() {
        val password = "TestPassword123"
        val salt = PasswordHasher.generateSalt()

        val hash =
            PasswordHasher.hashPassword(
                password,
                salt
            )

        val result =
            PasswordHasher.verifyPassword(
                password,
                salt,
                hash
            )

        assertTrue(result)
    }

    @Test
    fun verifyPassword_wrongPassword_returnsFalse() {
        val password = "TestPassword123"
        val wrongPassword = "WrongPassword123"

        val salt = PasswordHasher.generateSalt()

        val hash =
            PasswordHasher.hashPassword(
                password,
                salt
            )

        val result =
            PasswordHasher.verifyPassword(
                wrongPassword,
                salt,
                hash
            )

        assertFalse(result)
    }

    @Test
    fun generateSalt_differentCalls_produceDifferentSalts() {
        val salt1 =
            PasswordHasher.generateSalt()

        val salt2 =
            PasswordHasher.generateSalt()

        assertNotEquals(
            salt1,
            salt2
        )
    }

    @Test
    fun samePasswordAndSameSalt_produceSameHash() {
        val password = "TestPassword123"
        val salt = PasswordHasher.generateSalt()

        val hash1 =
            PasswordHasher.hashPassword(
                password,
                salt
            )

        val hash2 =
            PasswordHasher.hashPassword(
                password,
                salt
            )

        assertTrue(
            hash1 == hash2
        )
    }

    @Test
    fun samePasswordAndDifferentSalts_produceDifferentHashes() {
        val password = "TestPassword123"

        val salt1 =
            PasswordHasher.generateSalt()

        val salt2 =
            PasswordHasher.generateSalt()

        val hash1 =
            PasswordHasher.hashPassword(
                password,
                salt1
            )

        val hash2 =
            PasswordHasher.hashPassword(
                password,
                salt2
            )

        assertNotEquals(
            hash1,
            hash2
        )
    }
}