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

            // First prove the click works
            registerButton.text = "PROCESSING..."

            val nameText = name.text.toString().trim()
            val emailText = email.text.toString().trim()
            val phoneText = phone.text.toString().trim()
            val passwordText = password.text.toString()

            if (nameText.isEmpty() ||
                emailText.isEmpty() ||
                phoneText.isEmpty() ||
                passwordText.isEmpty()
            ) {

                registerButton.text = "REGISTER"

                loginText.text = "Please fill all fields ❌"

                return@setOnClickListener
            }

            if (passwordText.length < 6) {

                registerButton.text = "REGISTER"

                loginText.text =
                    "Password must contain at least 6 characters ❌"

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

                // SHOW RESULT DIRECTLY ON SCREEN
                loginText.text = message

                if (success) {

                    registerButton.text = "SUCCESS ✅"

                    // Wait so you can see SUCCESS
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