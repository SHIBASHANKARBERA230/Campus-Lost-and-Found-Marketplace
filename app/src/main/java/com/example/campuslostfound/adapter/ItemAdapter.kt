package com.example.campuslostfound.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuslostfound.R
import com.example.campuslostfound.database.ItemEntity

class ItemAdapter(
    private var items: List<ItemEntity>,
    private val onItemClick: (ItemEntity) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val ivItemImage: ImageView =
            itemView.findViewById(
                R.id.ivItemImage
            )

        val tvItemName: TextView =
            itemView.findViewById(
                R.id.tvItemName
            )

        val tvCategory: TextView =
            itemView.findViewById(
                R.id.tvCategory
            )

        val tvLocation: TextView =
            itemView.findViewById(
                R.id.tvLocation
            )

        val tvDate: TextView =
            itemView.findViewById(
                R.id.tvDate
            )

        val tvStatus: TextView =
            itemView.findViewById(
                R.id.tvStatus
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {

        val view =
            LayoutInflater.from(
                parent.context
            ).inflate(
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
            items[position]

        holder.tvItemName.text =
            item.name

        holder.tvCategory.text =
            "Category: ${item.category}"

        holder.tvLocation.text =
            "Location: ${item.location}"

        holder.tvDate.text =
            "Date: ${item.date}"

        holder.tvStatus.text =
            "Status: ${item.status}"

        if (
            !item.imageUri.isNullOrEmpty()
        ) {

            holder.ivItemImage
                .setImageURI(
                    Uri.parse(
                        item.imageUri
                    )
                )

        } else {

            holder.ivItemImage
                .setImageResource(
                    android.R.drawable.ic_menu_gallery
                )
        }

        holder.itemView.setOnClickListener {

            onItemClick(item)
        }
    }

    override fun getItemCount(): Int {

        return items.size
    }

    fun updateItems(
        newItems: List<ItemEntity>
    ) {

        items = newItems

        notifyDataSetChanged()
    }
}