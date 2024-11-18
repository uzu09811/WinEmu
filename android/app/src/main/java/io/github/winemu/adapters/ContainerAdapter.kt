
package io.github.winemu.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import io.github.winemu.R
import com.win_lib.container.Container

class ContainerAdapter(
    private val items: List<Container>,
    private val onItemClick: (Container) -> Unit
) : RecyclerView.Adapter<ContainerAdapter.ContainerViewHolder>() {

    // ViewHolder class to bind item views
    class ContainerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.containerIcon)
        val title: TextView = view.findViewById(R.id.containerTitle)
        val card: MaterialCardView = view.findViewById(R.id.item_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContainerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_container, parent, false)
        return ContainerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContainerViewHolder, position: Int) {
        val item = items[position]
        //holder.icon.setImageResource(item.iconResId) // Set the icon
        holder.title.text = item.getName()               // Set the title

        // Handle item click
        holder.card.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
