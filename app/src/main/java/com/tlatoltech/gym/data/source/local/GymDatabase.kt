package com.tlatoltech.gym.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tlatoltech.gym.data.source.local.dao.PlanDao
import com.tlatoltech.gym.data.source.local.dao.RoutineDao
import com.tlatoltech.gym.data.source.local.entity.Converters
import com.tlatoltech.gym.data.source.local.entity.ExerciseEntity
import com.tlatoltech.gym.data.source.local.entity.PlanEntity
import com.tlatoltech.gym.data.source.local.entity.RoutineEntity

@Database(
    entities = [PlanEntity::class, RoutineEntity::class, ExerciseEntity::class], // Las 3 tablas
    version = 2, // Subimos la versión porque hay tablas nuevas
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GymDatabase : RoomDatabase() {
    abstract fun planDao(): PlanDao

    abstract fun routineDao(): RoutineDao
}