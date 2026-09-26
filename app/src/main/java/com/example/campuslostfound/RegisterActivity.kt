package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.UserRepository
import com.example.campuslostfound.viewmodel.UserViewModel
import com.example.campuslostfound.viewmodel.UserViewModelFactory
import com.google.android.material.button.MaterialButton

class RegisterActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModels {
        UserViewModelFactory(
            UserRepository(
                AppDatabase.getDatabase(this).userDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        // Input fields
        val name = findViewById<EditText>(R.id.etName)
        val email = findViewById<EditText>(R.id.etEmail)
        val phone = findViewById<EditText>(R.id.etPhone)
        val password = findViewById<EditText>(R.id.etPassword)

        // Buttons
        val registerButton =
            findViewById<MaterialButton>(R.id.btnRegister)

        val googleButton =
            findViewById<MaterialButton>(R.id.btnGoogleRegister)

        // Message and Login
        val messageText =
            findViewById<TextView>(R.id.tvMessage)

        val loginText =
            findViewById<TextView>(R.id.tvLogin)

        // Register button
        registerButton.setOnClickListener {

            val nameText = name.text.toString().trim()
            val emailText = email.text.toString().trim()
            val phoneText = phone.text.toString().trim()
            val passwordText = password.text.toString()

            // Clear previous message
            messageText.text = ""

            // Validate required fields
            if (nameText.isEmpty() ||
                emailText.isEmpty() ||
                phoneText.isEmpty() ||
                passwordText.isEmpty()
            ) {

                messageText.text = getString(R.string.error_fill_all_fields)

                return@setOnClickListener
            }

            // Validate email
            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {

                messageText.text =
                    getString(R.string.error_invalid_email)

                email.requestFocus()

                return@setOnClickListener
            }

            // Validate phone
            if (!phoneText.matches(Regex("^[0-9]{10}$"))) {

                messageText.text =
                    getString(R.string.error_invalid_phone)

                phone.requestFocus()

                return@setOnClickListener
            }

            // Validate password
            if (passwordText.length < 6) {

                messageText.text =
                    getString(R.string.error_short_password)

                password.requestFocus()

                return@setOnClickListener
            }

            // Disable button while processing
            registerButton.isEnabled = false
            registerButton.text =
                getString(R.string.processing)

            // Register user
            viewModel.register(
                name = nameText,
                email = emailText,
                phone = phoneText,
                password = passwordText
            ) { success, message ->

                registerButton.isEnabled = true

                messageText.text = message

                if (success) {

                    registerButton.text =
                        getString(R.string.registration_success)

                    registerButton.postDelayed({

                        startActivity(
                            Intent(
                                this,
                                LoginActivity::class.java
                            )
                        )

                        finish()

                    }, 1200)

                } else {

                    registerButton.text =
                        getString(R.string.create_account)
                }
            }
        }

        // Google registration
        googleButton.setOnClickListener {

            messageText.text =
                getString(R.string.google_registration_coming_soon)

            // Real Google authentication will be implemented here later.
        }

        // Login
        loginText.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )

            finish()
        }
    }
}