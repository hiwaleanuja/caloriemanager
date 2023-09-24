package com.syracuse.caloriemanager.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Dairy (
    var breakfast: ArrayList<MealItem> = arrayListOf(),
    var lunch: ArrayList<MealItem> = arrayListOf(),
    var dinner: ArrayList<MealItem> = arrayListOf(),
) {
    constructor() : this(arrayListOf(), arrayListOf(), arrayListOf(),) {}

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "breakfast" to breakfast,
            "lunch" to lunch,
            "dinner" to dinner,
        )
    }

    @Exclude
    fun getDairyTotal(): MealItem {
        val accumulatorMealItem = MealItem()
        for (data in breakfast) {
            accumulatorMealItem.calories += data.calories
            accumulatorMealItem.proteins += data.proteins
            accumulatorMealItem.carbohydrates += data.carbohydrates
            accumulatorMealItem.fats += data.fats
        }
        for (data in lunch) {
            accumulatorMealItem.calories += data.calories
            accumulatorMealItem.proteins += data.proteins
            accumulatorMealItem.carbohydrates += data.carbohydrates
            accumulatorMealItem.fats += data.fats
        }
        for (data in dinner) {
            accumulatorMealItem.calories += data.calories
            accumulatorMealItem.proteins += data.proteins
            accumulatorMealItem.carbohydrates += data.carbohydrates
            accumulatorMealItem.fats += data.fats
        }
        return accumulatorMealItem
    }
}