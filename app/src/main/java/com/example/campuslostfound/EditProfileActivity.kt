package com.example.campuslostfound

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnSave: Button

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

        setContentView(R.layout.activity_edit_profile)

        etName =
            findViewById(R.id.etEditProfileName)

        etPhone =
            findViewById(R.id.etEditProfilePhone)

        btnSave =
            findViewById(R.id.btnSaveProfile)

        val preferences =
            getSharedPreferences(
                "user_session",
                MODE_PRIVATE
            )

        userId =
            preferences.getInt(
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

        loadUser()

        btnSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun loadUser() {

        lifecycleScope.launch {

            val user =
                withContext(Dispatchers.IO) {
                    userRepository.getUserById(userId)
                }

            if (user == null) {

                Toast.makeText(
                    this@EditProfileActivity,
                    "User not found",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
                return@launch
            }

            etName.setText(user.name)
            etPhone.setText(user.phone)
        }
    }

    private fun saveProfile() {

        val name =
            etName.text
                .toString()
                .trim()

        val phone =
            etPhone.text
                .toString()
                .trim()

        if (name.isEmpty()) {

            etName.error =
                "Name is required"

            etName.requestFocus()

            return
        }

        if (phone.isEmpty()) {

            etPhone.error =
                "Phone number is required"

            etPhone.requestFocus()

            return
        }

        if (!phone.matches(
                Regex("\\d{10}")
            )
        ) {

            etPhone.error =
                "Enter a valid 10 digit phone number"

            etPhone.requestFocus()

            return
        }

        lifecycleScope.launch {

            withContext(Dispatchers.IO) {

                userRepository.updateUserProfile(
                    userId = userId,
                    name = name,
                    phone = phone
                )
            }

            getSharedPreferences(
                "user_session",
                MODE_PRIVATE
            )
                .edit()
                .putString(
                    "userName",
                    name
                )
                .apply()

            Toast.makeText(
                this@EditProfileActivity,
                "Profile updated successfully",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }
    }
}