package com.example.campuslostfound.database

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun registerUser(
        user: UserEntity
    ): Long {
        return userDao.insertUser(user)
    }

    suspend fun loginUser(
        email: String
    ): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    suspend fun emailExists(
        email: String
    ): Boolean {
        return userDao.emailExists(email) > 0
    }

    suspend fun getUserById(
        userId: Int
    ): UserEntity? {
        return userDao.getUserById(userId)
    }

    suspend fun updateUserProfile(
        userId: Int,
        name: String,
        phone: String
    ) {
        userDao.updateUserProfile(
            userId,
            name,
            phone
        )
    }

    suspend fun updatePassword(
        userId: Int,
        passwordHash: String,
        passwordSalt: String
    ) {
        userDao.updatePassword(
            userId,
            passwordHash,
            passwordSalt
        )
    }
}