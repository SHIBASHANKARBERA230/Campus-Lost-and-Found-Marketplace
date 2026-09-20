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

    // Get user/owner by ID
    suspend fun getUserById(
        userId: Int
    ): UserEntity? {
        return userDao.getUserById(userId)
    }
}