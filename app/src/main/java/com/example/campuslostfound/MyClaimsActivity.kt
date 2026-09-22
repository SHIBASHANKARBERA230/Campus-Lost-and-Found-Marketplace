package com.example.campuslostfound

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslostfound.adapter.MyClaimsAdapter
import com.example.campuslostfound.database.AppDatabase
import com.example.campuslostfound.database.ClaimEntity
import com.example.campuslostfound.database.ClaimRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyClaimsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvResult: TextView

    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

    private val claimRepository by lazy {
        ClaimRepository(database.claimDao())
    }

    private val itemDao by lazy {
        database.itemDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_my_claims)

        recyclerView =
            findViewById(R.id.recyclerMyClaims)

        tvResult =
            findViewById(R.id.tvMyClaimsResult)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        loadMyClaims()
    }

    private fun loadMyClaims() {

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

            tvResult.text =
                "Please login first ❌"

            return
        }

        lifecycleScope.launch {

            val result =
                withContext(Dispatchers.IO) {

                    val claims =
                        claimRepository.getMyClaims(userId)

                    val itemNames =
                        mutableMapOf<Int, String>()

                    for (claim in claims) {

                        val item =
                            itemDao.getItemById(
                                claim.itemId
                            )

                        if (item != null) {

                            itemNames[claim.itemId] =
                                item.name
                        }
                    }

                    Pair(
                        claims,
                        itemNames
                    )
                }

            displayClaims(
                result.first,
                result.second
            )
        }
    }

    private fun displayClaims(
        claims: List<ClaimEntity>,
        itemNames: Map<Int, String>
    ) {

        if (claims.isEmpty()) {

            tvResult.text =
                "You have not submitted any claims yet."

            recyclerView.adapter =
                MyClaimsAdapter(
                    emptyList(),
                    emptyMap()
                ) { }

            return
        }

        tvResult.text =
            "${claims.size} claim(s)"

        recyclerView.adapter =
            MyClaimsAdapter(
                claims,
                itemNames
            ) { claim ->

                openItem(claim)
            }
    }

    private fun openItem(
        claim: ClaimEntity
    ) {

        val intent =
            Intent(
                this,
                ItemDetailsActivity::class.java
            )

        intent.putExtra(
            "itemId",
            claim.itemId
        )

        startActivity(intent)
    }

    override fun onResume() {

        super.onResume()

        if (::recyclerView.isInitialized) {
            loadMyClaims()
        }
    }
}