package com.example.campuslostfound

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.PasswordHasher
import com.example.campuslostfound.database.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var etCurrentPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnChangePassword: Button

    private val userRepository by lazy {
        UserRepository(
            AppDatabase
                .getDatabase(this)
                .userDao()
        )
    }

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_change_password)

        etCurrentPassword =
            findViewById(R.id.etCurrentPassword)

        etNewPassword =
            findViewById(R.id.etNewPassword)

        etConfirmPassword =
            findViewById(R.id.etConfirmPassword)

        btnChangePassword =
            findViewById(R.id.btnChangePassword)

        val preferences =
            getSharedPreferences(
                "user_session",
                MODE_PRIVATE
            )

        userId = preferences.getInt(
            "userId",
            -1
        )

        if (userId == -1) {
            Toast.makeText(
                this,
                "Please login again",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        btnChangePassword.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {

        val currentPassword =
            etCurrentPassword.text
                .toString()

        val newPassword =
            etNewPassword.text
                .toString()

        val confirmPassword =
            etConfirmPassword.text
                .toString()

        // -------------------------
        // VALIDATION
        // -------------------------

        if (currentPassword.isEmpty()) {

            etCurrentPassword.error =
                "Enter current password"

            etCurrentPassword.requestFocus()

            return
        }

        if (newPassword.isEmpty()) {

            etNewPassword.error =
                "Enter new password"

            etNewPassword.requestFocus()

            return
        }

        if (newPassword.length < 6) {

            etNewPassword.error =
                "Password must contain at least 6 characters"

            etNewPassword.requestFocus()

            return
        }

        if (confirmPassword.isEmpty()) {

            etConfirmPassword.error =
                "Confirm your new password"

            etConfirmPassword.requestFocus()

            return
        }

        if (newPassword != confirmPassword) {

            etConfirmPassword.error =
                "Passwords do not match"

            etConfirmPassword.requestFocus()

            return
        }

        if (currentPassword == newPassword) {

            etNewPassword.error =
                "New password must be different"

            etNewPassword.requestFocus()

            return
        }

        // -------------------------
        // DISABLE BUTTON
        // -------------------------

        btnChangePassword.isEnabled = false
        btnChangePassword.text =
            "CHANGING PASSWORD..."

        lifecycleScope.launch {

            val result =
                withContext(Dispatchers.IO) {

                    val user =
                        userRepository
                            .getUserById(userId)

                    if (user == null) {
                        return@withContext "USER_NOT_FOUND"
                    }

                    // -------------------------
                    // VERIFY CURRENT PASSWORD
                    // -------------------------

                    val currentPasswordCorrect =
                        PasswordHasher.verifyPassword(
                            password = currentPassword,
                            salt = user.passwordSalt,
                            storedHash = user.passwordHash
                        )

                    if (!currentPasswordCorrect) {
                        return@withContext "WRONG_PASSWORD"
                    }

                    // -------------------------
                    // GENERATE NEW SALT
                    // -------------------------

                    val newSalt =
                        PasswordHasher.generateSalt()

                    // -------------------------
                    // HASH NEW PASSWORD
                    // -------------------------

                    val newHash =
                        PasswordHasher.hashPassword(
                            password = newPassword,
                            saltString = newSalt
                        )

                    // -------------------------
                    // SAVE NEW PASSWORD
                    // -------------------------

                    userRepository.updatePassword(
                        userId = userId,
                        passwordHash = newHash,
                        passwordSalt = newSalt
                    )

                    "SUCCESS"
                }

            // -------------------------
            // ENABLE BUTTON
            // -------------------------

            btnChangePassword.isEnabled = true
            btnChangePassword.text =
                "🔐 CHANGE PASSWORD"

            when (result) {

                "SUCCESS" -> {

                    Toast.makeText(
                        this@ChangePasswordActivity,
                        "Password changed successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }

                "WRONG_PASSWORD" -> {

                    etCurrentPassword.error =
                        "Current password is incorrect"

                    etCurrentPassword.requestFocus()
                }

                "USER_NOT_FOUND" -> {

                    Toast.makeText(
                        this@ChangePasswordActivity,
                        "User not found",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
            }
        }
    }
}