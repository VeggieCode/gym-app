package com.tlatoltech.gym.domain.usecase

import com.tlatoltech.gym.domain.model.GymPlan
import com.tlatoltech.gym.domain.repository.GymPlanRepository
import kotlinx.coroutines.flow.Flow

class GetActiveGymPlansUseCase(
    private val repository: GymPlanRepository
) {
    operator fun invoke(): Flow<List<GymPlan>> {
        return repository.getActivePlans()
    }
}