package com.example.pinkbakeryplanner

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ShoppingListFragment : Fragment(R.layout.fragment_shopping_list) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.rvShopping)
        val btnClear = view.findViewById<Button>(R.id.btnClearChecked)

        rv.layoutManager = LinearLayoutManager(requireContext())

        val adapter = ShoppingAdapter(RecipeStorage.getShopping(requireContext())) { position, checked ->
            val list = RecipeStorage.getShopping(requireContext())
            val updated = list.toMutableList()
            val item = updated[position]
            updated[position] = item.copy(checked = checked)
            RecipeStorage.saveShopping(requireContext(), updated)
        }

        rv.adapter = adapter

        fun refresh() {
            adapter.submit(RecipeStorage.getShopping(requireContext()))
        }

        btnClear.setOnClickListener {
            val list = RecipeStorage.getShopping(requireContext())
            val remaining = list.filter { !it.checked }.toMutableList()
            RecipeStorage.saveShopping(requireContext(), remaining)
            refresh()
        }

        refresh()
    }
}
