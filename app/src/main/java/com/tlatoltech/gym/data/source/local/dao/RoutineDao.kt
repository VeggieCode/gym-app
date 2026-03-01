package com.tlatoltech.gym.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tlatoltech.gym.data.source.local.entity.ExerciseEntity
import com.tlatoltech.gym.data.source.local.entity.RoutineEntity
import com.tlatoltech.gym.data.source.local.entity.RoutineWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    @Transaction
    @Query("SELECT * FROM routines WHERE syncStatus != 'ARCHIVED'")
    fun observeActiveRoutines(): Flow<List<RoutineWithExercises>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity): Long

    // 👇 AGREGA : List<Long> AL FINAL DE ESTA LÍNEA 👇
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>): List<Long>

    @Transaction
    @Query("SELECT * FROM routines WHERE syncStatus = 'PENDING_CREATE'")
    suspend fun getPendingRoutines(): List<RoutineWithExercises>
}