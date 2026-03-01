package com.tlatoltech.gym.domain.usecase

import com.tlatoltech.gym.domain.exception.DomainException
import com.tlatoltech.gym.domain.model.GymPlan
import com.tlatoltech.gym.domain.repository.GymPlanRepository

// app/src/main/java/com/tugimnasio/app/feature/gymplans/domain/usecase/CreatePlanUseCase.kt

class CreatePlanUseCase(
    private val repository: GymPlanRepository
) {
    suspend operator fun invoke(name: String, level: String, price: Double): Result<GymPlan> {
        return try {
            // 1. Instanciamos la entidad.
            // Si el precio o nivel son inválidos, el bloque 'init' lanzará una DomainException.
            val plan = GymPlan(
                id = null,
                name = name,
                level = level,
                price = price
            )

            // 2. Si no hubo error, le pedimos a la infraestructura que lo guarde
            val savedPlan = repository.savePlan(plan)
            Result.success(savedPlan)

        } catch (e: DomainException) {
            // Atrapamos la regla de negocio rota y la enviamos al ViewModel
            Result.failure(e)
        } catch (e: Exception) {
            // Atrapamos errores genéricos (ej. sin internet)
            Result.failure(Exception("Error de conexión al crear el plan"))
        }
    }
}