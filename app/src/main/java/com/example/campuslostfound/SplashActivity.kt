package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_splash
        )

        Handler(
            Looper.getMainLooper()
        ).postDelayed({

            val preferences =
                getSharedPreferences(
                    "user_session",
                    MODE_PRIVATE
                )

            val isLoggedIn =
                preferences.getBoolean(
                    "isLoggedIn",
                    false
                )

            val nextActivity =
                if (isLoggedIn) {
                    MainActivity::class.java
                } else {
                    LoginActivity::class.java
                }

            startActivity(
                Intent(
                    this,
                    nextActivity
                )
            )

            finish()

        }, 2000)
    }
}