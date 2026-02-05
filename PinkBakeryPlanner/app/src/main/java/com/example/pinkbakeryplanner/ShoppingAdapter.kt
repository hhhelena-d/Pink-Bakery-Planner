package com.example.pinkbakeryplanner



import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShoppingAdapter(
    private var items: MutableList<ShoppingItem>,
    private val onToggle: (position: Int, checked: Boolean) -> Unit
) : RecyclerView.Adapter<ShoppingAdapter.VH>() {

    class VH(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.row_shopping_item, parent, false)
    ) {
        val cb: CheckBox = itemView.findViewById(R.id.cbDone)
        val tvItem: TextView = itemView.findViewById(R.id.tvItem)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(parent)

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.tvItem.text = item.name


        val amountText =
            if (item.unit == "pcs") "${item.amount.toInt()} ${item.unit}"
            else "${"%.0f".format(item.amount)} ${item.unit}"

        holder.tvAmount.text = amountText


        holder.cb.setOnCheckedChangeListener(null)
        holder.cb.isChecked = item.checked
        holder.cb.setOnCheckedChangeListener { _, isChecked ->
            onToggle(position, isChecked)
        }
    }

    override fun getItemCount(): Int = items.size

    fun submit(newItems: MutableList<ShoppingItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
