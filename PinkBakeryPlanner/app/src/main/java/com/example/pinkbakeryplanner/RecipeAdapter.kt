package com.example.pinkbakeryplanner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class RecipeAdapter(
    private var items: List<Recipe>,
    private val onClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.VH>() {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = itemView as MaterialCardView
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val tvMeta: TextView = itemView.findViewById(R.id.tvMeta)
        val tvNewest: TextView = itemView.findViewById(R.id.tvNewest) // NEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = items[position]

        holder.tvName.text = r.name
        holder.tvRating.text = "⭐ ${"%.1f".format(r.rating)}"
        holder.tvMeta.text = "⏱ ${r.prepTimeMinutes} min • 🍽 ${r.servings} servings • 🧾 ${r.ingredients.size} items"


        val isNewest = position == 0

        holder.tvNewest.visibility = if (isNewest) View.VISIBLE else View.GONE


        holder.card.strokeWidth = if (isNewest) 3 else 1


        holder.card.cardElevation = if (isNewest) 6f else 2f

        holder.card.setOnClickListener { onClick(r) }

        holder.card.strokeColor =
            if (isNewest) holder.itemView.resources.getColor(R.color.pink_primary, null)
            else holder.itemView.resources.getColor(android.R.color.darker_gray, null)

    }

    override fun getItemCount() = items.size

    fun submit(newItems: List<Recipe>) {
        items = newItems
        notifyDataSetChanged()
    }
}
