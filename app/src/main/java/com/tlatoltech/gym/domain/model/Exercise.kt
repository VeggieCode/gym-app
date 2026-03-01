package com.tlatoltech.gym.domain.model
data class Exercise(
    val id: Int? = null,
    val name: String,
    val sets: Int,
    val reps: Int
) {
    init {
        if (sets <= 0 || reps <= 0) throw IllegalArgumentException("Series y reps deben ser > 0")
    }
}