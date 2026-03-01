package com.tlatoltech.gym.data.source.remote.dto

data class RoutineDto(
    val id: Int? = null,
    val nombre: String,
    val dias_asignados: List<String>,
    val ejercicios: List<ExerciseDto>
)