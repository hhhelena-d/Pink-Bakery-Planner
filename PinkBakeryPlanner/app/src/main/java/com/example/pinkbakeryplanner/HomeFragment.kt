package com.example.pinkbakeryplanner

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var rv: RecyclerView
    private lateinit var tvCount: TextView
    private lateinit var tvEmpty: TextView
    private lateinit var etSearch: EditText
    private lateinit var adapter: RecipeAdapter

    private var allRecipes: List<Recipe> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = view.findViewById(R.id.rvRecipes)
        tvCount = view.findViewById(R.id.tvCount)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        etSearch = view.findViewById(R.id.etSearch)

        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecipeAdapter(emptyList()) { recipe ->
            RecipeStorage.setSelectedRecipeId(requireContext(), recipe.id)
            (requireActivity() as MainActivity).selectTab(R.id.recipeDetailsFragment)
        }
        rv.adapter = adapter


        allRecipes = RecipeStorage.getRecipes(requireContext())
        updateRecipeList(allRecipes)


        etSearch.addTextChangedListener(SimpleTextWatcher {
            val query = etSearch.text.toString().trim()
            val filtered = if (query.isEmpty()) {
                allRecipes
            } else {
                allRecipes.filter { it.name.contains(query, ignoreCase = true) }
            }
            updateRecipeList(filtered)
        })
    }

    override fun onResume() {
        super.onResume()
        // Reload recipes in case they changed
        allRecipes = RecipeStorage.getRecipes(requireContext())
        val query = etSearch.text.toString().trim()
        val filtered = if (query.isEmpty()) allRecipes else allRecipes.filter {
            it.name.contains(query, ignoreCase = true)
        }
        updateRecipeList(filtered)
    }

    private fun updateRecipeList(list: List<Recipe>) {
        adapter.submit(list)
        tvCount.text = "Saved: ${list.size}"

        if (list.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            rv.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            rv.visibility = View.VISIBLE
        }
    }
}
