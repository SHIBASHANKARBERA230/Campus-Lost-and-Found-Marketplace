package com.example.campuslostfound.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslostfound.R
import com.example.campuslostfound.database.NotificationEntity

class NotificationAdapter(
    private val notifications: List<NotificationEntity>,
    private val onNotificationClick:
        (NotificationEntity) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvTitle: TextView =
            itemView.findViewById(
                R.id.tvNotificationTitle
            )

        val tvMessage: TextView =
            itemView.findViewById(
                R.id.tvNotificationMessage
            )

        val tvStatus: TextView =
            itemView.findViewById(
                R.id.tvNotificationStatus
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_notification,
                parent,
                false
            )

        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: NotificationViewHolder,
        position: Int
    ) {

        val notification =
            notifications[position]

        holder.tvTitle.text =
            notification.title

        holder.tvMessage.text =
            notification.message

        holder.tvStatus.text =
            if (notification.isRead) {
                "Read"
            } else {
                "NEW"
            }

        if (!notification.isRead) {

            holder.tvTitle.setTypeface(
                null,
                Typeface.BOLD
            )

        } else {

            holder.tvTitle.setTypeface(
                null,
                Typeface.NORMAL
            )
        }

        holder.itemView.setOnClickListener {

            onNotificationClick(
                notification
            )
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }
}