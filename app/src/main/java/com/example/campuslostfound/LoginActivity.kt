package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse

import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.UserRepository
import com.example.campuslostfound.viewmodel.UserViewModel
import com.example.campuslostfound.viewmodel.UserViewModelFactory

import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    // =============================================================
    // USER VIEW MODEL
    // =============================================================

    private val viewModel: UserViewModel by viewModels {
        UserViewModelFactory(
            UserRepository(
                AppDatabase.getDatabase(this).userDao()
            )
        )
    }

    // =============================================================
    // GOOGLE CREDENTIAL MANAGER
    // =============================================================

    private lateinit var credentialManager: CredentialManager

    /*
     * Web OAuth Client ID
     *
     * This is the WEB client ID from Google Cloud.
     */
    private val webClientId =
        "242020444472-mobm8gecn6t9v14bmi51e0fbtktqqj6o.apps.googleusercontent.com"

    // =============================================================
    // ON CREATE
    // =============================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // =========================================================
        // CREDENTIAL MANAGER
        // =========================================================

        credentialManager = CredentialManager.create(this)

        // =========================================================
        // VIEWS
        // =========================================================

        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)

        val loginButton = findViewById<Button>(R.id.btnLogin)
        val googleButton = findViewById<Button>(R.id.btnGoogleLogin)

        val registerText = findViewById<TextView>(R.id.tvRegister)

        // =========================================================
        // NORMAL LOGIN
        // =========================================================

        loginButton.setOnClickListener {

            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString()

            // -----------------------------------------------------
            // EMPTY VALIDATION
            // -----------------------------------------------------

            if (emailText.isEmpty() || passwordText.isEmpty()) {

                registerText.text =
                    "Please enter email and password ❌"

                if (emailText.isEmpty()) {
                    email.requestFocus()
                } else {
                    password.requestFocus()
                }

                return@setOnClickListener
            }

            // -----------------------------------------------------
            // EMAIL VALIDATION
            // -----------------------------------------------------

            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {

                registerText.text =
                    "Please enter a valid email address ❌"

                email.requestFocus()

                return@setOnClickListener
            }

            // -----------------------------------------------------
            // PASSWORD VALIDATION
            // -----------------------------------------------------

            if (passwordText.length < 6) {

                registerText.text =
                    "Password must contain at least 6 characters ❌"

                password.requestFocus()

                return@setOnClickListener
            }

            // -----------------------------------------------------
            // LOGIN START
            // -----------------------------------------------------

            loginButton.isEnabled = false
            loginButton.text = "LOGGING IN..."

            viewModel.login(
                email = emailText,
                password = passwordText
            ) { success, user ->

                loginButton.isEnabled = true
                loginButton.text = "LOGIN"

                // =================================================
                // LOGIN SUCCESS
                // =================================================

                if (success && user != null) {

                    // -------------------------------------------------
                    // CHECK ACCOUNT STATUS
                    // -------------------------------------------------

                    if (!user.isActive) {

                        registerText.text =
                            "Your account has been deactivated ❌"

                        return@login
                    }

                    // -------------------------------------------------
                    // SAVE SESSION
                    // -------------------------------------------------

                    saveUserSession(
                        userId = user.id,
                        userName = user.name,
                        userEmail = user.email
                    )

                    registerText.text =
                        "Login successful ✅"

                    loginButton.text =
                        "SUCCESS ✅"

                    // -------------------------------------------------
                    // OPEN MAIN ACTIVITY
                    // -------------------------------------------------

                    loginButton.postDelayed({

                        openMainActivity()

                    }, 1200)

                } else {

                    // -------------------------------------------------
                    // LOGIN FAILED
                    // -------------------------------------------------

                    registerText.text =
                        "Invalid email or password ❌"
                }
            }
        }

        // =============================================================
        // GOOGLE LOGIN
        // =============================================================

        googleButton.setOnClickListener {

            googleButton.isEnabled = false
            googleButton.text = "OPENING GOOGLE..."

            CoroutineScope(Dispatchers.Main).launch {

                try {

                    signInWithGoogle()

                } catch (e: Exception) {

                    e.printStackTrace()

                    googleButton.isEnabled = true

                    googleButton.text =
                        "G   Continue with Google"

                    Toast.makeText(
                        this@LoginActivity,
                        "Google Sign-In failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // =============================================================
        // REGISTER
        // =============================================================

        registerText.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }
    }

    // =============================================================
    // GOOGLE SIGN-IN
    // =============================================================

    private suspend fun signInWithGoogle() {

        // -------------------------------------------------------------
        // CREATE GOOGLE ID OPTION
        // -------------------------------------------------------------

        val googleIdOption =
            GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

        // -------------------------------------------------------------
        // CREATE CREDENTIAL REQUEST
        // -------------------------------------------------------------

        val request =
            GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

        // -------------------------------------------------------------
        // OPEN GOOGLE ACCOUNT SELECTOR
        // -------------------------------------------------------------

        val result =
            credentialManager.getCredential(
                context = this,
                request = request
            )

        // -------------------------------------------------------------
        // PROCESS GOOGLE CREDENTIAL
        // -------------------------------------------------------------

        handleGoogleCredential(result)
    }

    // =============================================================
    // HANDLE GOOGLE CREDENTIAL
    // =============================================================

    private suspend fun handleGoogleCredential(
        result: GetCredentialResponse
    ) {

        val credential = result.credential

        // -------------------------------------------------------------
        // CHECK GOOGLE CREDENTIAL
        // -------------------------------------------------------------

        if (
            credential is CustomCredential &&
            credential.type ==
            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {

            try {

                // -----------------------------------------------------
                // CONVERT TO GOOGLE CREDENTIAL
                // -----------------------------------------------------

                val googleCredential =
                    GoogleIdTokenCredential
                        .createFrom(credential.data)

                // -----------------------------------------------------
                // GET GOOGLE USER NAME
                // -----------------------------------------------------

                val googleName =
                    googleCredential.displayName
                        ?: "Lostify User"

                // -----------------------------------------------------
                // GET GOOGLE EMAIL
                // -----------------------------------------------------

                val googleEmail =
                    googleCredential.id

                // =====================================================
                // FIND OR CREATE USER IN ROOM DATABASE
                // =====================================================

                val user =
                    withContext(Dispatchers.IO) {

                        UserRepository(
                            AppDatabase
                                .getDatabase(
                                    this@LoginActivity
                                )
                                .userDao()
                        ).getOrCreateGoogleUser(
                            name = googleName,
                            email = googleEmail
                        )
                    }

                // =====================================================
                // UPDATE UI ON MAIN THREAD
                // =====================================================

                withContext(Dispatchers.Main) {

                    // -------------------------------------------------
                    // CHECK ACCOUNT STATUS
                    // -------------------------------------------------

                    if (!user.isActive) {

                        Toast.makeText(
                            this@LoginActivity,
                            "Your account has been deactivated ❌",
                            Toast.LENGTH_LONG
                        ).show()

                        return@withContext
                    }

                    // =================================================
                    // SAVE REAL ROOM USER SESSION
                    // =================================================

                    saveUserSession(
                        userId = user.id,
                        userName = user.name,
                        userEmail = user.email
                    )

                    // -------------------------------------------------
                    // SUCCESS MESSAGE
                    // -------------------------------------------------

                    Toast.makeText(
                        this@LoginActivity,
                        "Welcome to Lostify, ${user.name}! ✅",
                        Toast.LENGTH_SHORT
                    ).show()

                    // -------------------------------------------------
                    // OPEN MAIN ACTIVITY
                    // -------------------------------------------------

                    openMainActivity()
                }

            } catch (e: GoogleIdTokenParsingException) {

                e.printStackTrace()

                withContext(Dispatchers.Main) {

                    Toast.makeText(
                        this@LoginActivity,
                        "Unable to process Google account.",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                e.printStackTrace()

                withContext(Dispatchers.Main) {

                    Toast.makeText(
                        this@LoginActivity,
                        "Google login failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        } else {

            // ---------------------------------------------------------
            // UNEXPECTED CREDENTIAL
            // ---------------------------------------------------------

            withContext(Dispatchers.Main) {

                Toast.makeText(
                    this@LoginActivity,
                    "Unexpected Google credential.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // =============================================================
    // SAVE USER SESSION
    // =============================================================

    private fun saveUserSession(
        userId: Int,
        userName: String,
        userEmail: String
    ) {

        val preferences =
            getSharedPreferences(
                "user_session",
                MODE_PRIVATE
            )

        preferences.edit()
            .putBoolean("isLoggedIn", true)
            .putInt("userId", userId)
            .putString("userName", userName)
            .putString("userEmail", userEmail)
            .apply()
    }

    // =============================================================
    // OPEN MAIN ACTIVITY
    // =============================================================

    private fun openMainActivity() {

        startActivity(
            Intent(
                this,
                MainActivity::class.java
            )
        )

        finish()
    }
}