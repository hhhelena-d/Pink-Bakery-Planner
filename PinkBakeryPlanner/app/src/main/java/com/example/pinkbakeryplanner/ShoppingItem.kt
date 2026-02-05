package com.example.pinkbakeryplanner



data class ShoppingItem(
    val name: String,
    val unit: String,
    val amount: Float,
    val checked: Boolean = false
)
