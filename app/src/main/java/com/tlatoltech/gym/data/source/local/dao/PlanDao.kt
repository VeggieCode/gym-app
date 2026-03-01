package com.tlatoltech.gym.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tlatoltech.gym.data.source.local.entity.PlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    // ROOM MÁGICO: Al retornar Flow, Room vigilará esta tabla.
    // Si algo cambia, emitirá una nueva lista automáticamente a la UI.
    @Query("SELECT * FROM planes WHERE isActive = 1")
    fun observeActivePlans(): Flow<List<PlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: PlanEntity): Long // Devuelve el nuevo localId

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plans: List<PlanEntity>): List<Long>

    @Update
    suspend fun updatePlan(plan: PlanEntity): Int

    // Borramos los sincronizados antes de meter los nuevos de Laravel
    @Query("DELETE FROM planes WHERE syncStatus = 'SYNCED'")
    suspend fun clearSyncedPlans(): Int // <--- CAMBIO AQUÍ

    // Método de ayuda para actualizar un registro una vez subido
    @Query("UPDATE planes SET syncStatus = 'SYNCED', remoteId = :newRemoteId WHERE localId = :localId")
    suspend fun markAsSynced(localId: Int, newRemoteId: Int): Int // <--- CAMBIO AQUÍ

    // Agrega esto en tu PlanDao
    @Query("SELECT * FROM planes WHERE syncStatus = 'PENDING_CREATE'")
    suspend fun getPendingPlans(): List<PlanEntity>
}