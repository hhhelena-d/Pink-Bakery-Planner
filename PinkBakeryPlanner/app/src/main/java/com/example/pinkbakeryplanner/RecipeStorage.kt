package com.example.pinkbakeryplanner

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object RecipeStorage {
    private const val PREFS = "pink_bakery_prefs"
    private const val KEY_RECIPES = "recipes"
    private const val KEY_SELECTED_ID = "selected_recipe_id"
    private const val KEY_SHOPPING = "shopping_items"
    private const val KEY_EDITING_ID = "editing_recipe_id"

    private val gson = Gson()



    fun getRecipes(context: Context): MutableList<Recipe> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_RECIPES, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Recipe>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun saveRecipes(context: Context, recipes: List<Recipe>) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_RECIPES, gson.toJson(recipes)).apply()
    }

    fun addRecipe(context: Context, recipe: Recipe) {
        val list = getRecipes(context)
        list.add(0, recipe)
        saveRecipes(context, list)
    }

    fun upsertRecipe(context: Context, recipe: Recipe) {
        val list = getRecipes(context)
        val idx = list.indexOfFirst { it.id == recipe.id }
        if (idx >= 0) list[idx] = recipe else list.add(0, recipe)
        saveRecipes(context, list)
    }

    fun deleteRecipe(context: Context, id: String) {
        val list = getRecipes(context).filter { it.id != id }
        saveRecipes(context, list)
    }



    fun setSelectedRecipeId(context: Context, id: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_SELECTED_ID, id).apply()
    }

    fun getSelectedRecipeId(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getString(KEY_SELECTED_ID, null)
    }

    fun getSelectedRecipe(context: Context): Recipe? {
        val id = getSelectedRecipeId(context)
        if (id.isNullOrBlank()) return null
        return getRecipes(context).firstOrNull { it.id == id }
    }

    fun clearSelectedRecipe(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit()
            .remove(KEY_SELECTED_ID)
            .remove(KEY_EDITING_ID)
            .apply()
    }



    fun getShopping(context: Context): MutableList<ShoppingItem> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SHOPPING, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<ShoppingItem>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun saveShopping(context: Context, items: List<ShoppingItem>) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_SHOPPING, gson.toJson(items)).apply()
    }

    fun addOrMergeShoppingItem(context: Context, newItem: ShoppingItem) {
        val list = getShopping(context)

        val idx = list.indexOfFirst {
            it.name.equals(newItem.name, ignoreCase = true) && it.unit == newItem.unit
        }

        if (idx >= 0) {
            val old = list[idx]
            list[idx] = old.copy(amount = old.amount + newItem.amount)
        } else {
            list.add(newItem)
        }

        saveShopping(context, list)
    }



    fun setEditingRecipeId(context: Context, id: String?) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_EDITING_ID, id).apply()
    }

    fun getEditingRecipeId(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getString(KEY_EDITING_ID, null)
    }

    fun getEditingRecipe(context: Context): Recipe? {
        val id = getEditingRecipeId(context)
        if (id.isNullOrBlank()) return null
        return getRecipes(context).firstOrNull { it.id == id }
    }
}
