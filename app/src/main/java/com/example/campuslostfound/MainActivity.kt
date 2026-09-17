package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val lostItems =
            findViewById<Button>(R.id.btnLostItems)

        val foundItems =
            findViewById<Button>(R.id.btnFoundItems)

        val postItem =
            findViewById<Button>(R.id.btnPostItem)

        val searchBox =
            findViewById<EditText>(R.id.etSearch)

        val profileButton =
            findViewById<Button>(R.id.btnProfile)

        lostItems.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    LostItemsActivity::class.java
                )
            )
        }

        foundItems.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    FoundItemsActivity::class.java
                )
            )
        }

        postItem.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    PostItemActivity::class.java
                )
            )
        }

        searchBox.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SearchActivity::class.java
                )
            )
        }

        profileButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }
    }
}