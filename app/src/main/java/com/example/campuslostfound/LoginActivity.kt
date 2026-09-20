package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.UserRepository
import com.example.campuslostfound.viewmodel.UserViewModel
import com.example.campuslostfound.viewmodel.UserViewModelFactory

class LoginActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModels {
        UserViewModelFactory(
            UserRepository(
                AppDatabase.getDatabase(this).userDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)

        val loginButton = findViewById<Button>(R.id.btnLogin)
        val registerText = findViewById<TextView>(R.id.tvRegister)

        // =========================
        // LOGIN
        // =========================

        loginButton.setOnClickListener {

            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString()

            if (emailText.isEmpty() || passwordText.isEmpty()) {

                registerText.text =
                    "Please enter email and password ❌"

                return@setOnClickListener
            }

            loginButton.isEnabled = false
            loginButton.text = "LOGGING IN..."

            viewModel.login(
                email = emailText,
                password = passwordText
            ) { success, user ->

                loginButton.isEnabled = true
                loginButton.text = "LOGIN"

                if (success && user != null) {

                    // Save session
                    val preferences =
                        getSharedPreferences(
                            "user_session",
                            MODE_PRIVATE
                        )

                    preferences.edit()
                        .putBoolean("isLoggedIn", true)
                        .putInt("userId", user.id)
                        .putString("userName", user.name)
                        .putString("userEmail", user.email)
                        .apply()

                    // SHOW SUCCESS ON SCREEN
                    registerText.text =
                        "Login successful ✅"

                    loginButton.text =
                        "SUCCESS ✅"

                    // Open MainActivity
                    loginButton.postDelayed({

                        startActivity(
                            Intent(
                                this,
                                MainActivity::class.java
                            )
                        )

                        finish()

                    }, 1200)

                } else {

                    registerText.text =
                        "Invalid email or password ❌"
                }
            }
        }

        // =========================
        // OPEN REGISTER
        // =========================

        registerText.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }
    }
}