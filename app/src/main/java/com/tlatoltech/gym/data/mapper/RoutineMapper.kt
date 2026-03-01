package com.tlatoltech.gym.data.mapper

import com.tlatoltech.gym.data.source.local.entity.ExerciseEntity
import com.tlatoltech.gym.data.source.local.entity.RoutineEntity
import com.tlatoltech.gym.data.source.local.entity.RoutineWithExercises
import com.tlatoltech.gym.data.source.remote.dto.ExerciseDto
import com.tlatoltech.gym.data.source.remote.dto.RoutineDto
import com.tlatoltech.gym.domain.model.Exercise
import com.tlatoltech.gym.domain.model.Routine

// 1. De Room (Tabla + Relación) a Dominio Puro
fun RoutineWithExercises.toDomain(): Routine {
    return Routine(
        id = this.routine.remoteId ?: this.routine.localId,
        name = this.routine.name,
        assignedDays = this.routine.assignedDays,
        exercises = this.exercises.map {
            Exercise(
                id = it.exerciseLocalId, // o remoteId si lo manejaras
                name = it.name,
                sets = it.sets,
                reps = it.reps
            )
        }
    )
}

// 2. De Dominio Puro a Entidad Principal de Room
fun Routine.toLocalEntity(syncStatus: String = "SYNCED"): RoutineEntity {
    return RoutineEntity(
        localId = if (this.id != null && syncStatus == "PENDING_CREATE") 0 else this.id ?: 0,
        remoteId = if (syncStatus == "PENDING_CREATE") null else this.id,
        name = this.name,
        assignedDays = this.assignedDays,
        syncStatus = syncStatus
    )
}

// 3. De Dominio Puro a DTO para enviar a Laravel (El JSON anidado)
fun Routine.toDto(): RoutineDto {
    return RoutineDto(
        id = this.id,
        nombre = this.name,
        dias_asignados = this.assignedDays,
        ejercicios = this.exercises.map {
            ExerciseDto(nombre = it.name, series = it.sets, repeticiones = it.reps)
        }
    )
}

fun RoutineDto.toDomain(): Routine {
    return Routine(
        id = this.id,
        name = this.nombre,
        assignedDays = this.dias_asignados,
        exercises = this.ejercicios.map { dto ->
            Exercise(
                id = dto.id,
                name = dto.nombre,
                sets = dto.series,
                reps = dto.repeticiones
            )
        }
    )
}