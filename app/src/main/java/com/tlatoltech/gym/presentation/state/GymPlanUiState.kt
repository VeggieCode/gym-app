package com.tlatoltech.gym.presentation.state

import com.tlatoltech.gym.domain.model.GymPlan

sealed interface GymPlanUiState {
    object Loading : GymPlanUiState
    data class Success(val gymPlans: List<GymPlan>) : GymPlanUiState
    data class Error(val message: String) : GymPlanUiState
}