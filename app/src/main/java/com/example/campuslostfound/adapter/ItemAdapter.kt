package com.example.campuslostfound.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslostfound.R
import com.example.campuslostfound.model.Item

class ItemAdapter(
    private val itemList: List<Item>,
    private val onItemClick: (Item) -> Unit = {}
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val itemName: TextView =
            itemView.findViewById(R.id.tvItemName)

        val category: TextView =
            itemView.findViewById(R.id.tvCategory)

        val location: TextView =
            itemView.findViewById(R.id.tvLocation)

        val date: TextView =
            itemView.findViewById(R.id.tvDate)

        val status: TextView =
            itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_lost_found,
                    parent,
                    false
                )

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {

        val item =
            itemList[position]

        holder.itemName.text =
            item.name

        holder.category.text =
            "Category: ${item.category}"

        holder.location.text =
            "📍 ${item.location}"

        holder.date.text =
            "📅 ${item.date}"

        holder.status.text =
            "Status: ${item.status}"

        holder.itemView.setOnClickListener {

            onItemClick(item)
        }
    }

    override fun getItemCount(): Int {

        return itemList.size
    }
}