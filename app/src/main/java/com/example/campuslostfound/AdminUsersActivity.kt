package com.example.campuslostfound

import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.campuslostfound.database.AdminActivityRepository
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.PasswordHasher
import com.example.campuslostfound.database.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminUsersActivity : AppCompatActivity() {

    private lateinit var usersContainer: LinearLayout

    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

    private val activityRepository by lazy {
        AdminActivityRepository(
            database.adminActivityDao()
        )
    }

    private var currentUserId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_admin_users
        )

        usersContainer =
            findViewById(R.id.usersContainer)

        val preferences =
            getSharedPreferences(
                "user_session",
                MODE_PRIVATE
            )

        currentUserId =
            preferences.getInt(
                "userId",
                0
            )

        if (currentUserId == 0) {

            Toast.makeText(
                this,
                "Please login first",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }

        checkAdmin()
    }


    // =============================================
    // CHECK ADMIN
    // =============================================

    private fun checkAdmin() {

        lifecycleScope.launch {

            val admin =
                withContext(Dispatchers.IO) {

                    database
                        .userDao()
                        .getAdminById(
                            currentUserId
                        )
                }

            if (admin == null) {

                Toast.makeText(
                    this@AdminUsersActivity,
                    "Admin access required",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

                return@launch
            }

            loadUsers()
        }
    }


    // =============================================
    // LOG ADMIN ACTIVITY
    // =============================================

    private fun logAdminActivity(
        action: String,
        targetUserId: Int? = null,
        details: String = ""
    ) {

        if (currentUserId == 0) {
            return
        }

        lifecycleScope.launch {

            withContext(Dispatchers.IO) {

                activityRepository.logActivity(
                    adminUserId = currentUserId,
                    action = action,
                    targetUserId = targetUserId,
                    details = details
                )
            }
        }
    }


    // =============================================
    // LOAD USERS
    // =============================================

    private fun loadUsers() {

        lifecycleScope.launch {

            val users =
                withContext(Dispatchers.IO) {

                    database
                        .userDao()
                        .getAllUsers()
                }

            displayUsers(users)
        }
    }


    // =============================================
    // DISPLAY USERS
    // =============================================

    private fun displayUsers(
        users: List<UserEntity>
    ) {

        usersContainer.removeAllViews()

        if (users.isEmpty()) {

            val emptyText =
                TextView(this)

            emptyText.text =
                "No registered users"

            emptyText.textSize =
                18f

            emptyText.setPadding(
                16,
                32,
                16,
                32
            )

            usersContainer.addView(
                emptyText
            )

            return
        }

        for (user in users) {

            addUserCard(user)
        }
    }


    // =============================================
    // USER CARD
    // =============================================

    private fun addUserCard(
        user: UserEntity
    ) {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.setPadding(
            20,
            20,
            20,
            20
        )


        // NAME

        val name =
            TextView(this)

        name.text =
            "👤 ${user.name}"

        name.textSize =
            20f

        name.setTypeface(
            null,
            Typeface.BOLD
        )


        // EMAIL

        val email =
            TextView(this)

        email.text =
            "📧 ${user.email}"

        email.textSize =
            15f


        // PHONE

        val phone =
            TextView(this)

        phone.text =
            "📱 ${user.phone}"

        phone.textSize =
            15f


        // ROLE

        val role =
            TextView(this)

        role.text =
            if (user.isAdmin) {
                "Role: ADMIN"
            } else {
                "Role: USER"
            }

        role.textSize =
            15f


        // STATUS

        val status =
            TextView(this)

        status.text =
            if (user.isActive) {
                "Status: ACTIVE"
            } else {
                "Status: INACTIVE"
            }

        status.textSize =
            15f


        // ADMIN BUTTON

        val adminButton =
            Button(this)

        adminButton.text =
            if (user.isAdmin) {
                "REMOVE ADMIN"
            } else {
                "MAKE ADMIN"
            }

        adminButton.setOnClickListener {

            if (user.id == currentUserId) {

                Toast.makeText(
                    this,
                    "You cannot change your own admin status",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (user.isAdmin) {

                removeAdmin(
                    user.id,
                    user.name
                )

            } else {

                makeAdmin(
                    user.id,
                    user.name
                )
            }
        }


        // ACTIVE BUTTON

        val activeButton =
            Button(this)

        activeButton.text =
            if (user.isActive) {
                "DEACTIVATE USER"
            } else {
                "ACTIVATE USER"
            }

        activeButton.setOnClickListener {

            if (user.id == currentUserId) {

                Toast.makeText(
                    this,
                    "You cannot deactivate yourself",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (user.isActive) {

                deactivateUser(
                    user.id,
                    user.name
                )

            } else {

                activateUser(
                    user.id,
                    user.name
                )
            }
        }


        // RESET PASSWORD BUTTON

        val resetPasswordButton =
            Button(this)

        resetPasswordButton.text =
            "RESET PASSWORD"

        resetPasswordButton.setOnClickListener {

            if (user.id == currentUserId) {

                Toast.makeText(
                    this,
                    "Use Change Password for your own account",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            showResetPasswordDialog(user)
        }


        // ADD VIEWS

        card.addView(name)

        card.addView(email)

        card.addView(phone)

        card.addView(role)

        card.addView(status)

        card.addView(adminButton)

        card.addView(activeButton)

        card.addView(resetPasswordButton)


        // CARD MARGIN

        val params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        params.setMargins(
            0,
            0,
            0,
            24
        )

        card.layoutParams =
            params

        usersContainer.addView(
            card
        )
    }


    // =============================================
    // RESET PASSWORD DIALOG
    // =============================================

    private fun showResetPasswordDialog(
        user: UserEntity
    ) {

        val passwordInput =
            EditText(this)

        passwordInput.hint =
            "Enter new password"

        passwordInput.inputType =
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD

        val container =
            LinearLayout(this)

        container.orientation =
            LinearLayout.VERTICAL

        container.setPadding(
            50,
            10,
            50,
            0
        )

        container.addView(
            passwordInput
        )

        AlertDialog.Builder(this)
            .setTitle(
                "Reset Password"
            )
            .setMessage(
                "Set a new password for ${user.name}"
            )
            .setView(container)
            .setNegativeButton(
                "CANCEL",
                null
            )
            .setPositiveButton(
                "RESET"
            ) { _, _ ->

                val newPassword =
                    passwordInput.text
                        .toString()

                if (newPassword.length < 6) {

                    Toast.makeText(
                        this,
                        "Password must contain at least 6 characters",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                resetPassword(
                    user.id,
                    user.name,
                    newPassword
                )
            }
            .show()
    }


    private fun resetPassword(
        userId: Int,
        userName: String,
        newPassword: String
    ) {

        lifecycleScope.launch {

            withContext(Dispatchers.IO) {

                val salt =
                    PasswordHasher.generateSalt()

                val hash =
                    PasswordHasher.hashPassword(
                        newPassword,
                        salt
                    )

                database
                    .userDao()
                    .updatePassword(
                        userId,
                        hash,
                        salt
                    )
            }

            logAdminActivity(
                action = "RESET_PASSWORD",
                targetUserId = userId,
                details =
                    "Administrator reset password for $userName"
            )

            Toast.makeText(
                this@AdminUsersActivity,
                "Password reset successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    // =============================================
    // MAKE ADMIN
    // =============================================

    private fun makeAdmin(
        userId: Int,
        userName: String
    ) {

        lifecycleScope.launch {

            val result =
                withContext(Dispatchers.IO) {

                    database
                        .userDao()
                        .makeUserAdmin(
                            userId
                        )
                }

            if (result > 0) {

                logAdminActivity(
                    action = "MAKE_ADMIN",
                    targetUserId = userId,
                    details =
                        "Granted administrator role to $userName"
                )

                Toast.makeText(
                    this@AdminUsersActivity,
                    "User promoted to admin",
                    Toast.LENGTH_SHORT
                ).show()

                loadUsers()

            } else {

                Toast.makeText(
                    this@AdminUsersActivity,
                    "Failed to make user admin",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    // =============================================
    // REMOVE ADMIN
    // =============================================

    private fun removeAdmin(
        userId: Int,
        userName: String
    ) {

        lifecycleScope.launch {

            val result =
                withContext(Dispatchers.IO) {

                    database
                        .userDao()
                        .removeAdmin(
                            userId
                        )
                }

            if (result > 0) {

                logAdminActivity(
                    action = "REMOVE_ADMIN",
                    targetUserId = userId,
                    details =
                        "Removed administrator role from $userName"
                )

                Toast.makeText(
                    this@AdminUsersActivity,
                    "Admin privilege removed",
                    Toast.LENGTH_SHORT
                ).show()

                loadUsers()

            } else {

                Toast.makeText(
                    this@AdminUsersActivity,
                    "Failed to remove admin privilege",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    // =============================================
    // DEACTIVATE USER
    // =============================================

    private fun deactivateUser(
        userId: Int,
        userName: String
    ) {

        lifecycleScope.launch {

            val result =
                withContext(Dispatchers.IO) {

                    database
                        .userDao()
                        .deactivateUser(
                            userId
                        )
                }

            if (result > 0) {

                logAdminActivity(
                    action = "DEACTIVATE_USER",
                    targetUserId = userId,
                    details =
                        "Deactivated user $userName"
                )

                Toast.makeText(
                    this@AdminUsersActivity,
                    "User deactivated",
                    Toast.LENGTH_SHORT
                ).show()

                loadUsers()

            } else {

                Toast.makeText(
                    this@AdminUsersActivity,
                    "Failed to deactivate user",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    // =============================================
    // ACTIVATE USER
    // =============================================

    private fun activateUser(
        userId: Int,
        userName: String
    ) {

        lifecycleScope.launch {

            val result =
                withContext(Dispatchers.IO) {

                    database
                        .userDao()
                        .activateUser(
                            userId
                        )
                }

            if (result > 0) {

                logAdminActivity(
                    action = "ACTIVATE_USER",
                    targetUserId = userId,
                    details =
                        "Activated user $userName"
                )

                Toast.makeText(
                    this@AdminUsersActivity,
                    "User activated",
                    Toast.LENGTH_SHORT
                ).show()

                loadUsers()

            } else {

                Toast.makeText(
                    this@AdminUsersActivity,
                    "Failed to activate user",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    // =============================================
    // REFRESH
    // =============================================

    override fun onResume() {

        super.onResume()

        if (currentUserId != 0) {

            loadUsers()
        }
    }
}