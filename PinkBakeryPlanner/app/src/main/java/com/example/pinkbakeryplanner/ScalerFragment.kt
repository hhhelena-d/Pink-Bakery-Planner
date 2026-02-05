package com.example.pinkbakeryplanner

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.slider.Slider

class ScalerFragment : Fragment(R.layout.fragment_scaler) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvBase = view.findViewById<TextView>(R.id.tvBase)
        val tvTarget = view.findViewById<TextView>(R.id.tvTarget)
        val tvResult = view.findViewById<TextView>(R.id.tvResult)
        val tvIngredients = view.findViewById<TextView>(R.id.tvIngredients)

        val slider = view.findViewById<Slider>(R.id.sliderServings)
        val btnAdd = view.findViewById<Button>(R.id.btnAddToList)

        val recipe = RecipeStorage.getSelectedRecipe(requireContext())

        if (recipe == null) {
            tvTitle.text = "Pick a recipe first 🍰"
            tvBase.text = ""
            tvTarget.text = ""
            tvResult.text = ""
            tvIngredients.text = ""
            slider.isEnabled = false
            btnAdd.isEnabled = false
            return
        }


        if (recipe.servings <= 0) {
            tvTitle.text = recipe.name
            tvBase.text = ""
            tvTarget.text = ""
            tvResult.text = ""
            tvIngredients.text = "This recipe has invalid servings. Edit it and set servings to 1–100."
            slider.isEnabled = false
            btnAdd.isEnabled = false
            return
        }

        tvTitle.text = recipe.name
        tvBase.text = "Base servings: ${recipe.servings}"


        val base = recipe.servings.coerceIn(1, 100)
        slider.value = base.toFloat()

        fun update(target: Int) {
            val safeTarget = target.coerceIn(1, 100)
            tvTarget.text = "Target servings: $safeTarget"

            val factor = safeTarget.toFloat() / recipe.servings.toFloat()
            tvResult.text = "Scaling factor: x${"%.2f".format(factor)}"

            val lines = recipe.ingredients.map { ing ->
                val amt = (ing.amount ?: 0f) * factor
                val amtText =
                    if (ing.unit == "pcs") "%.1f".format(amt)
                    else "%.0f".format(amt)

                val notePart =
                    if (ing.note.isNotBlank()) " (${ing.note})" else ""

                "- ${ing.name}: $amtText ${ing.unit}$notePart"
            }

            tvIngredients.text =
                if (lines.isEmpty()) {
                    "No ingredients to scale."
                } else {
                    "Scaled ingredients:\n" + lines.joinToString("\n")
                }
        }


        var currentTarget = base
        update(currentTarget)


        slider.clearOnChangeListeners()
        slider.addOnChangeListener { _, value, _ ->
            currentTarget = value.toInt().coerceIn(1, 100)
            update(currentTarget)
        }

        btnAdd.isEnabled = true
        btnAdd.setOnClickListener {
            val factor = currentTarget.toFloat() / recipe.servings.toFloat()

            recipe.ingredients.forEach { ing ->
                val amt = (ing.amount ?: 0f) * factor
                if (amt > 0f) {
                    RecipeStorage.addOrMergeShoppingItem(
                        requireContext(),
                        ShoppingItem(
                            name = ing.name,
                            unit = ing.unit,
                            amount = amt
                        )
                    )
                }
            }

            Toast.makeText(requireContext(), "Added to list 🛒", Toast.LENGTH_SHORT).show()
            (requireActivity() as MainActivity).selectTab(R.id.shoppingListFragment)
        }

    }
}
