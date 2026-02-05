package com.example.pinkbakeryplanner

import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class IngredientAdapter(
    private val items: MutableList<Ingredient>
) : RecyclerView.Adapter<IngredientAdapter.VH>() {

    private val units = listOf("g", "ml", "pcs", "tbsp", "tsp")

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tilName: TextInputLayout = itemView.findViewById(R.id.tilIngName)
        val etName: TextInputEditText = itemView.findViewById(R.id.etIngName)

        val tilAmount: TextInputLayout = itemView.findViewById(R.id.tilAmount)
        val etAmount: TextInputEditText = itemView.findViewById(R.id.etAmount)

        val actUnit: MaterialAutoCompleteTextView = itemView.findViewById(R.id.actUnit)


        val tilNote: TextInputLayout = itemView.findViewById(R.id.tilNote)
        val etNote: TextInputEditText = itemView.findViewById(R.id.etNote)

        val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)


        var nameWatcher: TextWatcher? = null
        var amountWatcher: TextWatcher? = null
        var noteWatcher: TextWatcher? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_ingredient, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ing = items[position]


        val unitAdapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_list_item_1, units)
        holder.actUnit.setAdapter(unitAdapter)


        holder.nameWatcher?.let { holder.etName.removeTextChangedListener(it) }
        holder.amountWatcher?.let { holder.etAmount.removeTextChangedListener(it) }
        holder.noteWatcher?.let { holder.etNote.removeTextChangedListener(it) }


        holder.etName.setText(ing.name)
        holder.etAmount.setText(ing.amount?.toString().orEmpty())
        holder.actUnit.setText(ing.unit, false)
        holder.etNote.setText(ing.note)


        holder.btnRemove.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                items.removeAt(pos)
                notifyItemRemoved(pos)
            }
        }


        holder.nameWatcher = SimpleTextWatcher {
            ing.name = holder.etName.text?.toString().orEmpty()
            holder.tilName.error = null
        }
        holder.etName.addTextChangedListener(holder.nameWatcher)

        holder.amountWatcher = SimpleTextWatcher {
            ing.amount = holder.etAmount.text?.toString()?.trim()?.toFloatOrNull()
            holder.tilAmount.error = null
        }
        holder.etAmount.addTextChangedListener(holder.amountWatcher)

        holder.noteWatcher = SimpleTextWatcher {
            ing.note = holder.etNote.text?.toString().orEmpty()
            holder.tilNote.error = null
        }
        holder.etNote.addTextChangedListener(holder.noteWatcher)

        holder.actUnit.setOnItemClickListener { _, _, _, _ ->
            ing.unit = holder.actUnit.text?.toString().orEmpty()
        }
    }

    override fun getItemCount() = items.size

    fun addRow() {
        items.add(Ingredient())
        notifyItemInserted(items.size - 1)
    }

    fun getItems(): List<Ingredient> = items

    val nameRegex = Regex("^[A-Za-z]+(\\s[A-Za-z]+)*$")


    fun validate(): Boolean {
        return items.isNotEmpty() && items.all { ing ->
            val name = ing.name.trim()
            val unit = ing.unit.trim()
            val amount = ing.amount

            nameRegex.matches(name) &&
                    unit.isNotEmpty() &&
                    (amount != null && amount > 0f)
        }
    }




    fun replaceAll(newItems: MutableList<Ingredient>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
