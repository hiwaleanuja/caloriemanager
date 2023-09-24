package com.syracuse.caloriemanager.models

data class FoodItem (
    val name: String,
    val unit: String,
    val calories: Int,
    val fats: Int,
    val proteins: Int,
    val carbohydrates: Int
)