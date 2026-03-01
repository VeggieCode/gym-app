package com.tlatoltech.gym.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tlatoltech.gym.domain.usecase.ArchivePlanUseCase
import com.tlatoltech.gym.domain.usecase.CreatePlanUseCase
import com.tlatoltech.gym.domain.usecase.GetActiveGymPlansUseCase
import com.tlatoltech.gym.presentation.state.GymPlanUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class GymPlanViewModel(
    private val getActiveGymPlansUseCase: GetActiveGymPlansUseCase,
    private val archiveGymPlanUseCase: ArchivePlanUseCase,
    private val createPlanUseCase: CreatePlanUseCase
) : ViewModel() {

    // Estado interno modificable
    private val _uiState = MutableStateFlow<GymPlanUiState>(GymPlanUiState.Loading)
    val uiState: StateFlow<GymPlanUiState> = _uiState.asStateFlow()

    private val _formError = MutableStateFlow<String?>(null)
    val formError: StateFlow<String?> = _formError.asStateFlow()

    init {
        loadActivePlans()
    }

    fun loadActivePlans() {
        viewModelScope.launch {
            _uiState.value = GymPlanUiState.Loading
            getActiveGymPlansUseCase()
                .catch { exception ->
                    _uiState.value = GymPlanUiState.Error(exception.message ?: "Error desconocido")
                }
                .collect { plans ->
                    _uiState.value = GymPlanUiState.Success(plans)
                }
        }
    }

    fun archivePlan(planId: String) {
        viewModelScope.launch {
            val result = archiveGymPlanUseCase(planId)
            result.onSuccess {
                // Al archivar exitosamente, la lista se actualizará automáticamente
                // gracias a que getActivePlans devuelve un Flow reactivo.
            }.onFailure { exception ->
                // Podríamos lanzar un evento para mostrar un Snackbar/Toast con el error
            }
        }
    }

    fun createPlan(name: String, level: String, priceStr: String, onSuccess: () -> Unit) {
        val price = priceStr.toDoubleOrNull()

        // Validación básica de formato en la capa de presentación
        if (price == null) {
            _formError.value = "Por favor, ingresa un precio numérico válido."
            return
        }

        viewModelScope.launch {
            val result = createPlanUseCase(name, level, price)

            result.onSuccess {
                _formError.value = null // Limpiamos errores
                loadActivePlans() // Recargamos la lista desde el backend
                onSuccess() // Le avisamos a la vista que ya terminó (para cerrar la pantalla)
            }.onFailure { exception ->
                // Atrapamos la DomainException (ej. InvalidPriceException) y la mostramos
                _formError.value = exception.message
            }
        }
    }

    // Método para limpiar el error si el usuario empieza a escribir de nuevo
    fun clearFormError() {
        _formError.value = null
    }
}