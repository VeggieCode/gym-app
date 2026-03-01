package com.tlatoltech.gym.data.repository

import com.tlatoltech.gym.domain.exception.PlanNotFoundException
import com.tlatoltech.gym.domain.model.GymPlan
import com.tlatoltech.gym.domain.repository.GymPlanRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class MockGymPlanRepository : GymPlanRepository {

    // Simulamos una base de datos en memoria
    private val memoryDatabase = MutableStateFlow(
        listOf(
            GymPlan(1, "Plan Básico Real", "Principiante", 29.99),
            GymPlan(2, "Plan Espartano", "Intermedio", 49.99),
            GymPlan(3, "Plan Élite Real", "Avanzado", 89.99)
        )
    )

    // Emitimos solo los planes que estén activos
    override fun getActivePlans(): Flow<List<GymPlan>> {
        return memoryDatabase.map { planes ->
            planes.filter { it.isActive }
        }
    }

    override suspend fun getPlanById(id: Int): GymPlan {
        return memoryDatabase.value.find { it.id == id }
            ?: throw PlanNotFoundException("Plan con ID $id no encontrado")
    }

    override suspend fun savePlan(plan: GymPlan): GymPlan {
        val currentList = memoryDatabase.value.toMutableList()
        val newId = (currentList.maxOfOrNull { it.id ?: 0 } ?: 0) + 1
        val newPlan = plan.copy(id = newId)

        currentList.add(newPlan)
        memoryDatabase.value = currentList // Actualiza el Flow

        return newPlan
    }

    override suspend fun updatePlan(plan: GymPlan) {
        val currentList = memoryDatabase.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == plan.id }

        if (index != -1) {
            currentList[index] = plan
            memoryDatabase.value = currentList // Dispara la reactividad en la UI
        }
    }
}