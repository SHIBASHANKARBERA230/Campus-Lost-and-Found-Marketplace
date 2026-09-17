package com.example.campuslostfound.database

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {

    private const val ITERATIONS = 120000
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16

    fun generateSalt(): String {

        val salt = ByteArray(SALT_LENGTH)

        SecureRandom().nextBytes(salt)

        return Base64.getEncoder()
            .encodeToString(salt)
    }

    fun hashPassword(
        password: String,
        saltString: String
    ): String {

        val salt = Base64.getDecoder()
            .decode(saltString)

        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATIONS,
            KEY_LENGTH
        )

        val factory =
            SecretKeyFactory.getInstance(
                "PBKDF2WithHmacSHA256"
            )

        val hash = factory
            .generateSecret(spec)
            .encoded

        return Base64.getEncoder()
            .encodeToString(hash)
    }

    fun verifyPassword(
        password: String,
        salt: String,
        storedHash: String
    ): Boolean {

        val newHash = hashPassword(
            password,
            salt
        )

        return newHash == storedHash
    }
}