package com.example.pinkbakeryplanner

data class Ingredient(
    var name: String = "",
    var unit: String = "g",
    var amount: Float? = null,
    var note: String = ""
)

