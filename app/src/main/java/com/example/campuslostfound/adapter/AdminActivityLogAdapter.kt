package com.example.campuslostfound.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslostfound.R
import com.example.campuslostfound.database.AdminActivityEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminActivityLogAdapter(
    private var activities: List<AdminActivityEntity>
) : RecyclerView.Adapter<AdminActivityLogAdapter.ActivityViewHolder>() {

    class ActivityViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvAction: TextView =
            itemView.findViewById(
                R.id.tvActivityAction
            )

        val tvDetails: TextView =
            itemView.findViewById(
                R.id.tvActivityDetails
            )

        val tvAdmin: TextView =
            itemView.findViewById(
                R.id.tvActivityAdmin
            )

        val tvDate: TextView =
            itemView.findViewById(
                R.id.tvActivityDate
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ActivityViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_admin_activity,
                    parent,
                    false
                )

        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ActivityViewHolder,
        position: Int
    ) {

        val activity =
            activities[position]

        holder.tvAction.text =
            "Action: ${activity.action}"

        holder.tvDetails.text =
            activity.details

        holder.tvAdmin.text =
            "Admin User ID: ${activity.adminUserId}"

        val formatter =
            SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.getDefault()
            )

        holder.tvDate.text =
            formatter.format(
                Date(activity.createdAt)
            )
    }

    override fun getItemCount(): Int {
        return activities.size
    }

    fun updateActivities(
        newActivities: List<AdminActivityEntity>
    ) {

        activities =
            newActivities

        notifyDataSetChanged()
    }
}