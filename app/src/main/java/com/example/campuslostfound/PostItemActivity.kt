package com.example.campuslostfound

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.ItemEntity
import com.example.campuslostfound.database.ItemRepository
import com.example.campuslostfound.viewmodel.ItemViewModel
import com.example.campuslostfound.viewmodel.ItemViewModelFactory

class PostItemActivity : AppCompatActivity() {

    private val viewModel: ItemViewModel by viewModels {

        ItemViewModelFactory(
            ItemRepository(
                AppDatabase
                    .getDatabase(this)
                    .itemDao()
            )
        )
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_post_item
        )

        val etItemName =
            findViewById<EditText>(
                R.id.etItemName
            )

        val etDescription =
            findViewById<EditText>(
                R.id.etDescription
            )

        val etCategory =
            findViewById<EditText>(
                R.id.etCategory
            )

        val etLocation =
            findViewById<EditText>(
                R.id.etLocation
            )

        val etDate =
            findViewById<EditText>(
                R.id.etDate
            )

        val rgType =
            findViewById<RadioGroup>(
                R.id.rgType
            )

        val btnSubmit =
            findViewById<Button>(
                R.id.btnSubmitItem
            )

        btnSubmit.setOnClickListener {

            val name =
                etItemName.text
                    .toString()
                    .trim()

            val description =
                etDescription.text
                    .toString()
                    .trim()

            val category =
                etCategory.text
                    .toString()
                    .trim()

            val location =
                etLocation.text
                    .toString()
                    .trim()

            val date =
                etDate.text
                    .toString()
                    .trim()

            val type =
                when (
                    rgType.checkedRadioButtonId
                ) {

                    R.id.rbFound -> "FOUND"

                    else -> "LOST"
                }

            if (
                name.isEmpty() ||
                description.isEmpty() ||
                category.isEmpty() ||
                location.isEmpty() ||
                date.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val preferences =
                getSharedPreferences(
                    "user_session",
                    MODE_PRIVATE
                )

            val userId =
                preferences.getInt(
                    "userId",
                    0
                )

            if (userId == 0) {

                Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            val item =
                ItemEntity(

                    userId = userId,

                    name = name,

                    description = description,

                    category = category,

                    type = type,

                    location = location,

                    date = date,

                    status = type
                )

            viewModel.insertItem(item)

            Toast.makeText(
                this,
                "$type item posted successfully!",
                Toast.LENGTH_LONG
            ).show()

            finish()
        }
    }
}