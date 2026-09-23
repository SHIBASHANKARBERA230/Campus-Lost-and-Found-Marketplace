package com.example.campuslostfound.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuslostfound.database.PasswordHasher
import com.example.campuslostfound.database.UserEntity
import com.example.campuslostfound.database.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {

    fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {

        viewModelScope.launch {

            try {

                // Check if email already exists
                val exists = withContext(Dispatchers.IO) {
                    repository.emailExists(email)
                }

                if (exists) {

                    withContext(Dispatchers.Main) {
                        onResult(
                            false,
                            "Email already registered ❌"
                        )
                    }

                    return@launch
                }


                // Generate random salt
                val salt = PasswordHasher.generateSalt()


                // Hash password
                val passwordHash = withContext(Dispatchers.IO) {

                    PasswordHasher.hashPassword(
                        password = password,
                        saltString = salt
                    )
                }


                // Create user
                val user = UserEntity(
                    name = name,
                    email = email,
                    phone = phone,
                    passwordHash = passwordHash,
                    passwordSalt = salt
                )


                // Insert user into Room
                val userId = withContext(Dispatchers.IO) {

                    repository.registerUser(user)
                }


                withContext(Dispatchers.Main) {

                    if (userId > 0) {

                        onResult(
                            true,
                            "Registration successful ✅"
                        )

                    } else {

                        onResult(
                            false,
                            "Registration failed ❌"
                        )
                    }
                }

            } catch (e: Exception) {

                e.printStackTrace()

                withContext(Dispatchers.Main) {

                    onResult(
                        false,
                        "Registration failed: ${e.message}"
                    )
                }
            }
        }
    }


    // =========================
    // LOGIN
    // =========================

    fun login(
        email: String,
        password: String,
        onResult: (Boolean, UserEntity?) -> Unit
    ) {

        viewModelScope.launch {

            try {

                // Find user by email
                val user = withContext(Dispatchers.IO) {

                    repository.loginUser(email)
                }


                // User not found
                if (user == null) {

                    withContext(Dispatchers.Main) {

                        onResult(
                            false,
                            null
                        )
                    }

                    return@launch
                }


                // Verify password
                val valid = withContext(Dispatchers.IO) {

                    PasswordHasher.verifyPassword(
                        password = password,
                        salt = user.passwordSalt,
                        storedHash = user.passwordHash
                    )
                }


                withContext(Dispatchers.Main) {

                    if (valid) {

                        onResult(
                            true,
                            user
                        )

                    } else {

                        onResult(
                            false,
                            null
                        )
                    }
                }

            } catch (e: Exception) {

                e.printStackTrace()

                withContext(Dispatchers.Main) {

                    onResult(
                        false,
                        null
                    )
                }
            }
        }
    }
}