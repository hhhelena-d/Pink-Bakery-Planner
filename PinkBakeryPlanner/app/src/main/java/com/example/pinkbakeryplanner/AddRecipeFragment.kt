package com.example.pinkbakeryplanner

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pinkbakeryplanner.databinding.FragmentAddRecipeBinding
import java.util.UUID

class AddRecipeFragment : Fragment(R.layout.fragment_add_recipe) {

    private var _binding: FragmentAddRecipeBinding? = null
    private val binding get() = _binding!!

    private lateinit var ingredientAdapter: IngredientAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddRecipeBinding.bind(view)


        ingredientAdapter = IngredientAdapter(mutableListOf(Ingredient(), Ingredient()))
        binding.rvIngredients.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIngredients.adapter = ingredientAdapter


        binding.btnAddIngredient.setOnClickListener { ingredientAdapter.addRow() }


        binding.btnNewRecipe.setOnClickListener {
            RecipeStorage.setEditingRecipeId(requireContext(), null)
            clearForm()
        }


        binding.etSteps.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }


        loadEditingRecipe()


        binding.btnSave.setOnClickListener { saveRecipe() }


        binding.etName.addTextChangedListener(SimpleTextWatcher { binding.tilName.error = null })
        binding.etPrepTime.addTextChangedListener(SimpleTextWatcher { binding.tilPrepTime.error = null })
        binding.etServings.addTextChangedListener(SimpleTextWatcher { binding.tilServings.error = null })
    }


    private fun loadEditingRecipe() {
        val editing = RecipeStorage.getEditingRecipe(requireContext())

        if (editing != null) {
            binding.btnNewRecipe.visibility = View.VISIBLE
            binding.btnSave.text = "Update Recipe"


            binding.etName.clearFocus()
            binding.etPrepTime.clearFocus()
            binding.etServings.clearFocus()
            binding.etSteps.clearFocus()


            binding.etName.post { binding.etName.setText(editing.name) }
            binding.etPrepTime.post { binding.etPrepTime.setText(editing.prepTimeMinutes.toString()) }
            binding.etServings.post { binding.etServings.setText(editing.servings.toString()) }
            binding.etSteps.post { binding.etSteps.setText(editing.stepsNote) }


            val list = editing.ingredients.map { ing ->
                Ingredient(
                    name = ing.name,
                    unit = ing.unit,
                    amount = ing.amount,
                    note = ing.note
                )
            }.toMutableList()

            val finalList = if (list.isEmpty()) mutableListOf(Ingredient(), Ingredient()) else list
            ingredientAdapter.replaceAll(finalList)


            binding.rvIngredients.post { binding.rvIngredients.scrollToPosition(0) }
        } else {
            clearForm()
        }
    }


    private fun clearForm() {
        binding.tilName.error = null
        binding.tilPrepTime.error = null
        binding.tilServings.error = null
        binding.tilSteps.error = null

        binding.etName.setText("")
        binding.etPrepTime.setText("")
        binding.etServings.setText("")
        binding.etSteps.setText("")
        binding.etName.requestFocus()

        ingredientAdapter.replaceAll(mutableListOf(Ingredient(), Ingredient()))
        binding.btnSave.text = "Save Recipe"
        binding.btnNewRecipe.visibility = View.GONE
    }


    private fun validateForm(): Boolean {
        var ok = true
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        val prepStr = binding.etPrepTime.text?.toString()?.trim().orEmpty()
        val servingsStr = binding.etServings.text?.toString()?.trim().orEmpty()

        val letterCount = name.count { it.isLetter() }
        if (name.length < 3 || letterCount < 3) {
            binding.tilName.error = "Name must contain at least 3 letters"
            ok = false
        } else {
            val allRecipes = RecipeStorage.getRecipes(requireContext())
            val editingId = RecipeStorage.getEditingRecipe(requireContext())?.id
            val duplicate = allRecipes.any { r -> r.name.equals(name, ignoreCase = true) && r.id != editingId }
            if (duplicate) {
                binding.tilName.error = "A recipe with this name already exists"
                ok = false
            } else binding.tilName.error = null
        }

        val prep = prepStr.toIntOrNull()
        if (prep == null) {
            binding.tilPrepTime.error = "Enter a valid number"
            ok = false
        } else if (prep !in 1..999) {
            binding.tilPrepTime.error = "Prep time must be 1–999 minutes"
            ok = false
        } else binding.tilPrepTime.error = null

        val servings = servingsStr.toIntOrNull()
        if (servings == null) {
            binding.tilServings.error = "Enter a valid number"
            ok = false
        } else if (servings !in 1..100) {
            binding.tilServings.error = "Servings must be 1–100"
            ok = false
        } else binding.tilServings.error = null

        return ok
    }


    private fun saveRecipe() {
        if (!validateForm()) return

        binding.rvIngredients.clearFocus()

        val rawIngredients = ingredientAdapter.getItems()
        val ingredients = rawIngredients
            .filter { it.name.isNotBlank() || it.amount != null || it.unit.isNotBlank() || it.note.isNotBlank() }
            .map { ing -> Ingredient(name = ing.name.trim(), unit = ing.unit.trim(), amount = ing.amount, note = ing.note.trim()) }

        if (ingredients.isEmpty()) {
            Toast.makeText(requireContext(), "Add at least one ingredient 🍰", Toast.LENGTH_SHORT).show()
            return
        }

        val nameRegex = Regex("^[A-Za-z]+(\\s[A-Za-z]+)*$")
        val invalid = ingredients.firstOrNull { ing ->
            val amt = ing.amount
            ing.name.isEmpty() || !nameRegex.matches(ing.name) || ing.unit.isEmpty() || amt == null || amt <= 0f
        }
        if (invalid != null) {
            Toast.makeText(
                requireContext(),
                "Invalid ingredient: '${invalid.name}'\nNames must contain only letters and spaces, unit cannot be empty, amount > 0",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val recipeName = binding.etName.text!!.toString().trim()
        val prep = binding.etPrepTime.text!!.toString().toInt()
        val servings = binding.etServings.text!!.toString().toInt()
        val steps = binding.etSteps.text?.toString()?.trim().orEmpty()

        val editingRecipe = RecipeStorage.getEditingRecipe(requireContext())
        val id = editingRecipe?.id ?: UUID.randomUUID().toString()

        val recipe = Recipe(
            id = id,
            name = recipeName,
            prepTimeMinutes = prep,
            servings = servings,
            rating = editingRecipe?.rating ?: 0f,
            ingredients = ingredients,
            stepsNote = steps
        )

        RecipeStorage.upsertRecipe(requireContext(), recipe)
        RecipeStorage.setSelectedRecipeId(requireContext(), id)
        RecipeStorage.setEditingRecipeId(requireContext(), null)

        Toast.makeText(requireContext(), "Saved 🍰", Toast.LENGTH_SHORT).show()
        (requireActivity() as MainActivity).selectTab(R.id.recipeDetailsFragment)

        if (editingRecipe == null) clearForm()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
