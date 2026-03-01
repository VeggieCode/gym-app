package com.tlatoltech.gym.data.repository

import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import com.tlatoltech.gym.data.mapper.toDomain
import com.tlatoltech.gym.data.mapper.toDto
import com.tlatoltech.gym.data.mapper.toLocalEntity
import com.tlatoltech.gym.data.source.local.dao.RoutineDao
import com.tlatoltech.gym.data.source.local.entity.ExerciseEntity
import com.tlatoltech.gym.data.source.remote.GymRoutineApiService
import com.tlatoltech.gym.domain.model.Routine
import com.tlatoltech.gym.domain.repository.RoutineRepository
import com.tlatoltech.gym.framework.worker.RoutineSyncWorker
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
            Log.e("RoutineSync", "Offline. Encolando WorkManager para intentar luego.", e)

            // Configuramos las condiciones para el Worker
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // ¡Solo si hay internet!
                .build()

            // Creamos la petición de trabajo de un solo uso
            val syncWorkRequest = OneTimeWorkRequestBuilder<RoutineSyncWorker>()
                .setConstraints(constraints)
                .build()
        }

        return routineWithLocalId
    }

    override suspend fun syncPendingRoutines() {
        // 1. Buscamos todas las rutinas atascadas en SQLite
        val pendingRoutines = localDao.getPendingRoutines()

        for (pending in pendingRoutines) {
            try {
                val pureDomainRoutine = pending.toDomain()

                // 2. Intentamos enviarlas a Laravel
                val response = apiService.createRoutine(pureDomainRoutine.toDto())

                if (response.success && response.data != null) {
                    // 3. Si Laravel responde OK, la marcamos como SYNCED con su ID real
                    val confirmedDomain = response.data.toDomain()
                    // Usamos el localId original para sobrescribir y no duplicar
                    val entityToUpdate = confirmedDomain.toLocalEntity(syncStatus = "SYNCED").copy(localId = pending.routine.localId)

                    localDao.insertRoutine(entityToUpdate)
                }
            } catch (e: Exception) {
                // Si falla, no hacemos nada, la rutina seguirá siendo PENDING_CREATE
                // para el próximo intento del WorkManager.
            }
        }
    }
}