package com.syracuse.caloriemanager.models

enum class ActivityLevel(val value: Double, val description: String) {
    NO_EXERCISE(1.2, "Little or no exercise"),
    LITTLE_EXERCISE(1.375, "Light exercise a few times a week"),
    MODERATE_EXERCISE(1.55, "Moderate exercise 3 to 5 times a week"),
    HEAVY_EXERCISE(1.725, "Heavy exercise 6 to 7 times per week");

    override fun toString() : String {
        return description
    }
}