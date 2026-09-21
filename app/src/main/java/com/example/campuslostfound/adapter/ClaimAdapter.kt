package com.example.campuslostfound.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslostfound.R
import com.example.campuslostfound.database.ClaimEntity

class ClaimAdapter(
    private val claims: List<ClaimEntity>,
    private val onApprove: (ClaimEntity) -> Unit,
    private val onReject: (ClaimEntity) -> Unit
) : RecyclerView.Adapter<ClaimAdapter.ClaimViewHolder>() {

    class ClaimViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvItemName: TextView =
            itemView.findViewById(R.id.tvClaimItemName)

        val tvReason: TextView =
            itemView.findViewById(R.id.tvClaimReason)

        val tvDetails: TextView =
            itemView.findViewById(R.id.tvClaimDetails)

        val tvStatus: TextView =
            itemView.findViewById(R.id.tvClaimStatus)

        val btnApprove: Button =
            itemView.findViewById(R.id.btnApproveClaim)

        val btnReject: Button =
            itemView.findViewById(R.id.btnRejectClaim)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClaimViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_claim,
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

        holder.tvItemName.text =
            "Item ID: ${claim.itemId}"

        holder.tvReason.text =
            "Reason: ${claim.reason}"

        holder.tvDetails.text =
            "Details: ${claim.additionalDetails}"

        holder.tvStatus.text =
            "Status: ${claim.status}"

        val pending = claim.status == "PENDING"

        holder.btnApprove.visibility =
            if (pending) View.VISIBLE else View.GONE

        holder.btnReject.visibility =
            if (pending) View.VISIBLE else View.GONE

        holder.btnApprove.setOnClickListener {
            onApprove(claim)
        }

        holder.btnReject.setOnClickListener {
            onReject(claim)
        }
    }

    override fun getItemCount(): Int {
        return claims.size
    }
}