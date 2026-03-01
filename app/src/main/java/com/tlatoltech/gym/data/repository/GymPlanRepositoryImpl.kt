package com.tlatoltech.gym.data.repository

import com.tlatoltech.gym.data.mapper.toDomain
import com.tlatoltech.gym.data.mapper.toLocalEntity
import com.tlatoltech.gym.data.source.local.dao.PlanDao
import com.tlatoltech.gym.data.source.remote.GymPlanApiService
import com.tlatoltech.gym.data.source.remote.dto.toDomain
import com.tlatoltech.gym.data.source.remote.dto.toDto
import com.tlatoltech.gym.domain.exception.PlanNotFoundException
import com.tlatoltech.gym.domain.model.GymPlan
import com.tlatoltech.gym.domain.repository.GymPlanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import android.util.Log // Agrega este import

class GymPlanRepositoryImpl(
    private val localDao: PlanDao,
    private val apiService: GymPlanApiService
) : GymPlanRepository {

    override fun getActivePlans(): Flow<List<GymPlan>> {
        val localFlow = localDao.observeActivePlans().map { entities ->
            entities.map { it.toDomain() }
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. SUBIR LOS PENDIENTES (Delta Sync)
                val pendingPlans = localDao.getPendingPlans()
                for (pendingPlan in pendingPlans) {
                    try {
                        // Intentamos mandarlos a Laravel uno por uno
                        val response = apiService.createPlan(pendingPlan.toDomain().toDto())
                        if (response.success && response.data != null) {
                            // Si Laravel lo acepta, actualizamos la base local con su ID real de MySQL
                            localDao.markAsSynced(
                                localId = pendingPlan.localId,
                                newRemoteId = response.data.id ?: 0
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("Sincronizacion", "Fallo al subir plan pendiente: ${pendingPlan.name}")
                    }
                }

                // 2. DESCARGAR LOS MÁS RECIENTES DE LARAVEL
                val response = apiService.getActivePlans()
                if (response.success && response.data != null) {
                    val remotePlans = response.data.map { it.toDomain() }

                    localDao.clearSyncedPlans()
                    localDao.insertAll(remotePlans.map { it.toLocalEntity(syncStatus = "SYNCED") })
                }
            } catch (e: Exception) {
                Log.e("Sincronizacion", "Fallo la sincronizacion general: ${e.message}")
            }
        }

        return localFlow
    }

    override suspend fun savePlan(plan: GymPlan): GymPlan {
        val pendingEntity = plan.toLocalEntity(syncStatus = "PENDING_CREATE")
        val localId = localDao.insertPlan(pendingEntity)
        val planWithLocalId = plan.copy(id = localId.toInt())

        try {
            val response = apiService.createPlan(planWithLocalId.toDto())
            if (response.success && response.data != null) {
                val confirmedDomain = response.data.toDomain()
                // Si tiene éxito, lo marcamos como SYNCED
                localDao.insertPlan(confirmedDomain.toLocalEntity(syncStatus = "SYNCED"))
                return confirmedDomain
            }
        } catch (e: Exception) {
            // AQUÍ ESTÁ LA MAGIA: Imprimimos el error en la consola de Android Studio
            Log.e("Sincronizacion", "Fallo al enviar a Laravel: ${e.message}", e)
        }

        return planWithLocalId
    }

    override suspend fun updatePlan(plan: GymPlan) {
        if (!plan.isActive) {
            // Si la entidad pura dice que no está activa, llamamos a la ruta de archivar
            val response = apiService.archivePlan(plan.id ?: throw Exception("ID nulo"))
            if (!response.success) {
                throw Exception(response.message ?: "Error al archivar en el servidor")
            }
        }
    }

    override suspend fun getPlanById(id: Int): GymPlan {
        // Para simplificar, obtenemos la lista y filtramos.
        // En un caso real, tendrías un endpoint @GET("planes/{id}") en Laravel.
        val response = apiService.getActivePlans()
        val dto = response.data?.find { it.id == id }
            ?: throw PlanNotFoundException("Plan no encontrado en el servidor")
        return dto.toDomain()
    }
}