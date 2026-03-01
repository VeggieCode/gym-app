package com.tlatoltech.gym.domain.usecase

import com.tlatoltech.gym.domain.exception.DomainException
import com.tlatoltech.gym.domain.repository.GymPlanRepository

class ArchivePlanUseCase(
    private val repository: GymPlanRepository
) {
    suspend operator fun invoke(planId: String): Result<Unit> {
        return try {
            // 1. Obtenemos la Entidad Pura
            val plan = repository.getPlanById(planId.toInt())

            // 2. Ejecutamos el comportamiento.
            // Si ya estaba inactivo, lanzará PlanAlreadyInactiveException.
            val archivedPlan = plan.archive()

            // 3. Persistimos el nuevo estado inmutable
            repository.updatePlan(archivedPlan)

            Result.success(Unit)

        } catch (e: DomainException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(Exception("Error de red al intentar archivar el plan"))
        }
    }
}