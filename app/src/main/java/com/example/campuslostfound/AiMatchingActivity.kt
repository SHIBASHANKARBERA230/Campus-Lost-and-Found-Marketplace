package com.example.campuslostfound

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.campuslostfound.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AiMatchingActivity : AppCompatActivity() {

    private lateinit var ivLostImage: ImageView
    private lateinit var ivFoundImage: ImageView
    private lateinit var btnSelectLost: Button
    private lateinit var btnSelectFound: Button
    private lateinit var btnFindMatch: Button
    private lateinit var tvResult: TextView

    private var lostImageUri: Uri? = null
    private var foundImageUri: Uri? = null


    // ============================================================
    // LOST IMAGE PICKER
    // ============================================================

    private val lostImagePicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                lostImageUri = uri

                ivLostImage.setImageURI(uri)

                Toast.makeText(
                    this,
                    "Lost item image selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    // ============================================================
    // FOUND IMAGE PICKER
    // ============================================================

    private val foundImagePicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                foundImageUri = uri

                ivFoundImage.setImageURI(uri)

                Toast.makeText(
                    this,
                    "Found item image selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    // ============================================================
    // ON CREATE
    // ============================================================

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ai_matching)


        // Find views

        ivLostImage =
            findViewById(R.id.ivLostImage)

        ivFoundImage =
            findViewById(R.id.ivFoundImage)

        btnSelectLost =
            findViewById(R.id.btnSelectLost)

        btnSelectFound =
            findViewById(R.id.btnSelectFound)

        btnFindMatch =
            findViewById(R.id.btnFindMatch)

        tvResult =
            findViewById(R.id.tvResult)


        // ========================================================
        // SELECT LOST IMAGE
        // ========================================================

        btnSelectLost.setOnClickListener {

            lostImagePicker.launch("image/*")
        }


        // ========================================================
        // SELECT FOUND IMAGE
        // ========================================================

        btnSelectFound.setOnClickListener {

            foundImagePicker.launch("image/*")
        }


        // ========================================================
        // FIND MATCH
        // ========================================================

        btnFindMatch.setOnClickListener {

            if (lostImageUri == null) {

                Toast.makeText(
                    this,
                    "Please select the lost item image",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            if (foundImageUri == null) {

                Toast.makeText(
                    this,
                    "Please select the found item image",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            compareImages()
        }
    }


    // ============================================================
    // COMPARE IMAGES
    // ============================================================

    private fun compareImages() {

        btnFindMatch.isEnabled = false

        tvResult.text = "🤖 AI is comparing the images...\n\nPlease wait..."


        CoroutineScope(Dispatchers.IO).launch {

            try {

                val lostUri = lostImageUri!!
                val foundUri = foundImageUri!!


                // Convert URI → File

                val lostFile =
                    createTempFileFromUri(
                        lostUri,
                        "lost_item"
                    )

                val foundFile =
                    createTempFileFromUri(
                        foundUri,
                        "found_item"
                    )


                // =================================================
                // LOST IMAGE REQUEST BODY
                // =================================================

                val lostRequestBody =
                    lostFile.asRequestBody(
                        "image/*".toMediaTypeOrNull()
                    )


                // =================================================
                // FOUND IMAGE REQUEST BODY
                // =================================================

                val foundRequestBody =
                    foundFile.asRequestBody(
                        "image/*".toMediaTypeOrNull()
                    )


                // =================================================
                // MULTIPART LOST IMAGE
                // =================================================

                val lostPart =
                    MultipartBody.Part.createFormData(
                        "lostImage",
                        lostFile.name,
                        lostRequestBody
                    )


                // =================================================
                // MULTIPART FOUND IMAGE
                // =================================================

                val foundPart =
                    MultipartBody.Part.createFormData(
                        "foundImage",
                        foundFile.name,
                        foundRequestBody
                    )


                // =================================================
                // CALL SPRING BOOT
                // =================================================

                val response =
                    RetrofitClient
                        .aiApiService
                        .compareImages(
                            lostPart,
                            foundPart
                        )


                // =================================================
                // RETURN TO MAIN THREAD
                // =================================================

                withContext(Dispatchers.Main) {

                    btnFindMatch.isEnabled = true


                    if (response.isSuccessful) {

                        val result =
                            response.body()
                                ?: "No result received."

                        tvResult.text =
                            formatResult(result)

                    } else {

                        tvResult.text =
                            "AI comparison failed.\n\n" +
                                    "HTTP Code: ${response.code()}\n\n" +
                                    response.errorBody()
                                        ?.string()
                    }
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {

                    btnFindMatch.isEnabled = true

                    tvResult.text =
                        "Connection error:\n\n${e.message}"
                }
            }
        }
    }


    // ============================================================
    // URI → TEMP FILE
    // ============================================================

    private fun createTempFileFromUri(
        uri: Uri,
        prefix: String
    ): File {

        val inputStream =
            contentResolver.openInputStream(uri)
                ?: throw Exception(
                    "Unable to open selected image."
                )


        val file =
            File.createTempFile(
                prefix,
                ".jpg",
                cacheDir
            )


        inputStream.use { input ->

            file.outputStream().use { output ->

                input.copyTo(output)
            }
        }


        return file
    }


    // ============================================================
    // FORMAT GEMINI RESULT
    // ============================================================

    private fun formatResult(
        result: String
    ): String {

        return "🤖 AI MATCH RESULT\n\n$result"
    }
}