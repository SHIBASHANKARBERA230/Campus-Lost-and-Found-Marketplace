package com.example.campuslostfound.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslostfound.R
import com.example.campuslostfound.database.ClaimEntity

class MyClaimsAdapter(
    private val claims: List<ClaimEntity>,
    private val itemNames: Map<Int, String>,
    private val onViewItem: (ClaimEntity) -> Unit
) : RecyclerView.Adapter<MyClaimsAdapter.ClaimViewHolder>() {

    class ClaimViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvItemName: TextView =
            view.findViewById(R.id.tvMyClaimItemName)

        val tvReason: TextView =
            view.findViewById(R.id.tvMyClaimReason)

        val tvDetails: TextView =
            view.findViewById(R.id.tvMyClaimDetails)

        val tvStatus: TextView =
            view.findViewById(R.id.tvMyClaimStatus)

        val tvDate: TextView =
            view.findViewById(R.id.tvMyClaimDate)

        val btnViewItem: Button =
            view.findViewById(R.id.btnViewMyClaimItem)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClaimViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_my_claim,
                parent,
                false
            )

        return ClaimViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ClaimViewHolder,
        position: Int
    ) {

        val claim = claims[position]

        val itemName =
            itemNames[claim.itemId] ?: "Unknown Item"

        holder.tvItemName.text =
            "📦 $itemName"

        holder.tvReason.text =
            "Reason: ${claim.reason}"

        holder.tvDetails.text =
            "Details: ${claim.additionalDetails}"

        holder.tvStatus.text =
            "Status: ${getStatusText(claim.status)}"

        holder.tvDate.text =
            "Claim ID: ${claim.id}"

        holder.btnViewItem.setOnClickListener {
            onViewItem(claim)
        }
    }

    private fun getStatusText(status: String): String {

        return when (status) {

            "PENDING" ->
                "PENDING ⏳"

            "APPROVED" ->
                "APPROVED ✅"

            "REJECTED" ->
                "REJECTED ❌"

            else ->
                status
        }
    }

    override fun getItemCount(): Int {
        return claims.size
    }
}