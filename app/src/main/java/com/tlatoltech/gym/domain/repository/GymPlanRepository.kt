package com.tlatoltech.gym.domain.repository

import com.tlatoltech.gym.domain.model.GymPlan
import kotlinx.coroutines.flow.Flow

interface GymPlanRepository {
    fun getActivePlans(): Flow<List<GymPlan>>

    // Lanza PlanNotFoundException si falla en la red o base de datos local
    suspend fun getPlanById(id: Int): GymPlan

    suspend fun savePlan(plan: GymPlan): GymPlan

    suspend fun updatePlan(plan: GymPlan)
}