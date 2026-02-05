package com.example.pinkbakeryplanner

data class Recipe(
    val id: String,
    val name: String,
    val prepTimeMinutes: Int,
    val servings: Int,
    val rating: Float = 0f,
    val ingredients: List<Ingredient> = emptyList(),
    val stepsNote: String = ""
)
