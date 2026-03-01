package com.tlatoltech.gym.domain.model

data class Routine(
    val id: Int? = null,
    val name: String,
    val assignedDays: List<String>,
    val exercises: List<Exercise> // La relación 1 a N
) {
    init {
        if (exercises.isEmpty()) throw IllegalArgumentException("La rutina debe tener ejercicios")
    }
}