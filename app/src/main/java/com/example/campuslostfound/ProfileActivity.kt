package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        val preferences =
            getSharedPreferences(
                "user_session",
                MODE_PRIVATE
            )

        val userName =
            preferences.getString(
                "userName",
                "User"
            )

        val userEmail =
            preferences.getString(
                "userEmail",
                ""
            )

        findViewById<TextView>(
            R.id.tvProfileName
        ).text = userName

        findViewById<TextView>(
            R.id.tvProfileEmail
        ).text = userEmail

        findViewById<Button>(
            R.id.btnLogout
        ).setOnClickListener {

            preferences.edit()
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
}