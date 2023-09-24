package com.syracuse.caloriemanager.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable

@JsonIgnoreProperties("selection")
class MealItem: Serializable {
    var name: String = ""
    var unit: String = ""
    var count: Int = 0
    var calories: Int = 0
    var proteins: Int = 0
    var fats: Int = 0
    var carbohydrates: Int = 0
}