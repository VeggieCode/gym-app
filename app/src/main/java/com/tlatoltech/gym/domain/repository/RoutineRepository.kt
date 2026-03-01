package com.tlatoltech.gym.domain.repository

import com.tlatoltech.gym.domain.model.Routine
import kotlinx.coroutines.flow.Flow

interface RoutineRepository {
    // Devuelve un flujo constante de datos ensamblados para la UI
    fun observeActiveRoutines(): Flow<List<Routine>>

    // Recibe una Rutina pura y se encarga de todo el guardado complejo
    suspend fun saveRoutine(routine: Routine): Routine
}
