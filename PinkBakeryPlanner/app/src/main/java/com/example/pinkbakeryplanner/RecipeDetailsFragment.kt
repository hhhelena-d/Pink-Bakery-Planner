package com.example.pinkbakeryplanner

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class RecipeDetailsFragment : Fragment(R.layout.fragment_recipe_details) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvPrep = view.findViewById<TextView>(R.id.tvPrep)
        val tvServings = view.findViewById<TextView>(R.id.tvServings)
        val tvIngredients = view.findViewById<TextView>(R.id.tvIngredients)
        val tvSteps = view.findViewById<TextView>(R.id.tvSteps) // NEW
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)

        val btnEdit = view.findViewById<Button>(R.id.btnEdit)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)

        val recipe = RecipeStorage.getSelectedRecipe(requireContext())

        if (recipe == null) {
            tvTitle.text = "Pick a recipe first 🍰"
            tvPrep.text = ""
            tvServings.text = ""
            tvIngredients.text = ""
            tvSteps.text = ""
            ratingBar.isEnabled = false
            btnEdit.isEnabled = false
            btnDelete.isEnabled = false
            return
        }


        tvTitle.text = recipe.name
        tvPrep.text = "Prep time: ${recipe.prepTimeMinutes} minutes"
        tvServings.text = "Serves: ${recipe.servings}"


        val lines = recipe.ingredients.map { ing ->
            val amt = ing.amount ?: 0f
            val amtText = if (ing.unit == "pcs") amt.toInt().toString() else "%.0f".format(amt)

            val note = ing.note.trim()
            val notePart = if (note.isNotEmpty()) " (${note})" else ""

            "• ${ing.name} — $amtText ${ing.unit}$notePart"
        }

        tvIngredients.text = if (lines.isEmpty()) {
            "No ingredients saved yet."
        } else {
            "Ingredients:\n" + lines.joinToString("\n")
        }


        val steps = recipe.stepsNote.trim()
        tvSteps.text = if (steps.isEmpty()) {
            "Steps:\n(No steps added yet.)"
        } else {
            "Steps:\n$steps"
        }


        btnEdit.isEnabled = true
        btnEdit.setOnClickListener {
            RecipeStorage.setEditingRecipeId(requireContext(), recipe.id)
            (requireActivity() as MainActivity).selectTab(R.id.addRecipeFragment)
        }


        btnDelete.isEnabled = true
        btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete recipe?")
                .setMessage("This will permanently remove \"${recipe.name}\".")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete") { _, _ ->
                    RecipeStorage.deleteRecipe(requireContext(), recipe.id)
                    RecipeStorage.clearSelectedRecipe(requireContext())
                    Toast.makeText(requireContext(), "Recipe deleted 🗑️", Toast.LENGTH_SHORT).show()
                    (requireActivity() as MainActivity).selectTab(R.id.homeFragment)
                }
                .show()
        }


        ratingBar.isEnabled = true
        ratingBar.setOnRatingBarChangeListener(null)
        ratingBar.rating = recipe.rating

        ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (!fromUser) return@setOnRatingBarChangeListener

            val list = RecipeStorage.getRecipes(requireContext())
            val updated = list.map {
                if (it.id == recipe.id) it.copy(rating = rating) else it
            }
            RecipeStorage.saveRecipes(requireContext(), updated)
        }

    }
}
