package com.tlatoltech.gym.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["localId"],
            childColumns = ["routineLocalId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val exerciseLocalId: Int = 0,
    val routineLocalId: Int, // Llave foránea que apunta al localId de la rutina
    val name: String,
    val sets: Int,
    val reps: Int
)