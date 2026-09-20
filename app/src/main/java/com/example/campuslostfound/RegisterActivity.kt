package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.UserRepository
import com.example.campuslostfound.viewmodel.UserViewModel
import com.example.campuslostfound.viewmodel.UserViewModelFactory

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

        val name = findViewById<EditText>(R.id.etName)
        val email = findViewById<EditText>(R.id.etEmail)
        val phone = findViewById<EditText>(R.id.etPhone)
        val password = findViewById<EditText>(R.id.etPassword)

        val registerButton = findViewById<Button>(R.id.btnRegister)
        val loginText = findViewById<TextView>(R.id.tvLogin)

        registerButton.setOnClickListener {

            registerButton.text = "PROCESSING..."

            val nameText = name.text.toString().trim()
            val emailText = email.text.toString().trim()
            val phoneText = phone.text.toString().trim()
            val passwordText = password.text.toString()

            // Validate required fields
            if (nameText.isEmpty() ||
                emailText.isEmpty() ||
                phoneText.isEmpty() ||
                passwordText.isEmpty()
            ) {

                registerButton.text = "REGISTER"

                loginText.text = "Please fill all fields ❌"

                return@setOnClickListener
            }

            // Validate email format
            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {

                registerButton.text = "REGISTER"

                loginText.text =
                    "Please enter a valid email address ❌"

                email.requestFocus()

                return@setOnClickListener
            }

            // Validate phone number
            if (!phoneText.matches(Regex("^[0-9]{10}$"))) {

                registerButton.text = "REGISTER"

                loginText.text =
                    "Phone number must contain 10 digits ❌"

                phone.requestFocus()

                return@setOnClickListener
            }

            // Validate password length
            if (passwordText.length < 6) {

                registerButton.text = "REGISTER"

                loginText.text =
                    "Password must contain at least 6 characters ❌"

                password.requestFocus()

                return@setOnClickListener
            }

            registerButton.isEnabled = false

            viewModel.register(
                name = nameText,
                email = emailText,
                phone = phoneText,
                password = passwordText
            ) { success, message ->

                registerButton.isEnabled = true
                registerButton.text = "REGISTER"

                // Show registration result
                loginText.text = message

                if (success) {

                    registerButton.text = "SUCCESS ✅"

                    // Open Login screen after successful registration
                    registerButton.postDelayed({

                        startActivity(
                            Intent(
                                this,
                                LoginActivity::class.java
                            )
                        )

                        finish()

                    }, 1200)
                }
            }
        }

        // Open Login screen
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