package com.syracuse.caloriemanager.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var firstName: String = "",
    var lastName: String = "",
    var gender: String = "",
    var age: Double = 0.0,
    var height: Double = 0.0,
    var currentWeight: Double = 0.0,
    var goalWeight: Double = 0.0,
    var activityLevel: String = "NO_EXERCISE",
    var isOnBoarded: Boolean = false,
) {
    constructor() : this("", "", "", 0.0, 0.0, 0.0, 0.0, ActivityLevel.NO_EXERCISE.toString()) {}

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "gender" to gender,
            "age" to age,
            "height" to height,
            "currentWeight" to currentWeight,
            "goalWeight" to goalWeight,
            "activityLevel" to activityLevel
        )
    }
}
