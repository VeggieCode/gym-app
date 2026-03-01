package com.tlatoltech.gym.data.source.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/*
El Secreto de Room: La clase @Relation
Para no tener que hacer consultas manuales complejas con JOINs, Room nos ofrece una estructura de
datos especial. Esta clase no es una tabla, es solo un "molde" para que Room sepa cómo ensamblar el
resultado de un solo golpe.
 */
data class RoutineWithExercises(
    @Embedded val routine: RoutineEntity,

    @Relation(
        parentColumn = "localId",
        entityColumn = "routineLocalId"
    )
    val exercises: List<ExerciseEntity>
)