package com.tlatoltech.gym.data.repository

import android.util.Log
import com.tlatoltech.gym.data.mapper.toDomain
import com.tlatoltech.gym.data.mapper.toDto
import com.tlatoltech.gym.data.mapper.toLocalEntity
import com.tlatoltech.gym.data.source.local.dao.RoutineDao
import com.tlatoltech.gym.data.source.local.entity.ExerciseEntity
import com.tlatoltech.gym.data.source.remote.GymRoutineApiService
import com.tlatoltech.gym.domain.model.Routine
import com.tlatoltech.gym.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoutineRepositoryImpl(
    private val localDao: RoutineDao,
    private val apiService: GymRoutineApiService
) : RoutineRepository {

    override fun observeActiveRoutines(): Flow<List<Routine>> {
        // Room nos devuelve el Agregado completo mágicamente gracias a @Relation
        return localDao.observeActiveRoutines().map { relations ->
            relations.map { it.toDomain() }
        }
    }

    override suspend fun saveRoutine(routine: Routine): Routine {
        // PASO 1: Guardado Local (Offline-First)
        // Guardamos la raíz (Routine)
        val pendingRoutineEntity = routine.toLocalEntity(syncStatus = "PENDING_CREATE")
        val generatedLocalId = localDao.insertRoutine(pendingRoutineEntity)

        // Preparamos los ejercicios asignándoles el ID de la rutina que acabamos de crear
        val exerciseEntities = routine.exercises.map { exercise ->
            ExerciseEntity(
                routineLocalId = generatedLocalId.toInt(), // Llave foránea
                name = exercise.name,
                sets = exercise.sets,
                reps = exercise.reps
            )
        }

        // Guardamos los hijos de un solo golpe
        localDao.insertExercises(exerciseEntities)

        // Actualizamos nuestra entidad pura con el ID local temporal
        val routineWithLocalId = routine.copy(id = generatedLocalId.toInt())

        // PASO 2: Sincronización con Laravel
        try {
            // Mandamos el JSON anidado y complejo a Laravel
            val response = apiService.createRoutine(routineWithLocalId.toDto())

            if (response.success && response.data != null) {
                // Si Laravel lo acepta, actualizamos el estado local a SYNCED con el ID real
                val confirmedDomain = response.data.toDomain()
                localDao.insertRoutine(confirmedDomain.toLocalEntity(syncStatus = "SYNCED"))
                return confirmedDomain
            }
        } catch (e: Exception) {
            Log.e("RoutineSync", "Guardado localmente. Pendiente de enviar a Laravel.", e)
            // No arrojamos la excepción para no romper la experiencia offline
        }

        return routineWithLocalId
    }
}