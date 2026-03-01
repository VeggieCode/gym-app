package com.tlatoltech.gym.domain.usecase

import com.tlatoltech.gym.domain.model.Exercise
import com.tlatoltech.gym.domain.model.Routine
import com.tlatoltech.gym.domain.repository.RoutineRepository

class CreateRoutineUseCase(private val repository: RoutineRepository) {

    suspend operator fun invoke(
        name: String,
        assignedDays: List<String>,
        exercisesData: List<Triple<String, Int, Int>> // Nombre, Series, Reps
    ): Result<Routine> {
        return try {
            // 1. Construimos los hijos (Ejercicios)
            val pureExercises = exercisesData.map { (exName, sets, reps) ->
                Exercise(name = exName, sets = sets, reps = reps) // Puede lanzar excepción
            }

            // 2. Construimos la Raíz (Rutina)
            val routine = Routine(
                name = name,
                assignedDays = assignedDays,
                exercises = pureExercises
            ) // Puede lanzar excepción si la lista está vacía

            // 3. Enviamos al repositorio (que hará el guardado local y luego la petición a Laravel)
            val savedRoutine = repository.saveRoutine(routine)
            Result.success(savedRoutine)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}