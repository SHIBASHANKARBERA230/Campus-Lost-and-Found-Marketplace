package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileEmail: TextView
    private lateinit var tvProfilePhone: TextView
    private lateinit var btnAdminReports: Button

    private val userRepository by lazy {
        UserRepository(
            AppDatabase
                .getDatabase(this)
                .userDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        tvProfileName =
            findViewById(R.id.tvProfileName)

        tvProfileEmail =
            findViewById(R.id.tvProfileEmail)

        tvProfilePhone =
            findViewById(R.id.tvProfilePhone)

        btnAdminReports =
            findViewById(R.id.btnAdminReports)

        // EDIT PROFILE
        findViewById<Button>(
            R.id.btnEditProfile
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    EditProfileActivity::class.java
                )
            )
        }

        // CHANGE PASSWORD
        findViewById<Button>(
            R.id.btnChangePassword
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ChangePasswordActivity::class.java
                )
            )
        }

        // ADMIN REPORTS
        btnAdminReports.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    AdminReportsActivity::class.java
                )
            )
        }

        // LOGOUT
        findViewById<Button>(
            R.id.btnLogout
        ).setOnClickListener {

            logout()
        }
    }

    override fun onResume() {

        super.onResume()

        loadProfile()
    }

    private fun loadProfile() {

        val preferences =
            getSharedPreferences(
                "user_session",
                MODE_PRIVATE
            )

        val userId =
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

        lifecycleScope.launch {

            val user =
                withContext(Dispatchers.IO) {

                    userRepository
                        .getUserById(userId)
                }

            if (user == null) {

                Toast.makeText(
                    this@ProfileActivity,
                    "User not found",
                    Toast.LENGTH_SHORT
                ).show()

                return@launch
            }

            tvProfileName.text =
                user.name

            tvProfileEmail.text =
                user.email

            tvProfilePhone.text =
                user.phone

            // SHOW ADMIN REPORTS ONLY FOR ADMIN
            if (user.isAdmin) {

                btnAdminReports.visibility =
                    android.view.View.VISIBLE

            } else {

                btnAdminReports.visibility =
                    android.view.View.GONE
            }
        }
    }

    private fun logout() {

        val preferences =
            getSharedPreferences(
                "user_session",
                MODE_PRIVATE
            )

        preferences
            .edit()
            .clear()
            .apply()

        startActivity(
            Intent(
                this,
                LoginActivity::class.java
            )
        )

        finishAffinity()
    }
}