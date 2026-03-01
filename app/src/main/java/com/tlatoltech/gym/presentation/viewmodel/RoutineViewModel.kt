package com.tlatoltech.gym.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tlatoltech.gym.domain.usecase.CreateRoutineUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

// Una clase temporal solo para la UI, para saber qué está escribiendo el usuario
data class ExerciseFormState(
    val id: String = UUID.randomUUID().toString(), // ID temporal para Compose
    val name: String = "",
    val sets: String = "",
    val reps: String = ""
)

class RoutineViewModel(
    private val createRoutineUseCase: CreateRoutineUseCase
) : ViewModel() {

    private val _formError = MutableStateFlow<String?>(null)
    val formError: StateFlow<String?> = _formError.asStateFlow()

    // Estado de la lista dinámica de ejercicios en el formulario
    private val _exercisesForm = MutableStateFlow<List<ExerciseFormState>>(listOf(ExerciseFormState()))
    val exercisesForm: StateFlow<List<ExerciseFormState>> = _exercisesForm.asStateFlow()

    fun addExerciseField() {
        _exercisesForm.value = _exercisesForm.value + ExerciseFormState()
    }

    fun removeExerciseField(id: String) {
        _exercisesForm.value = _exercisesForm.value.filter { it.id != id }
    }

    fun updateExerciseField(id: String, newName: String, newSets: String, newReps: String) {
        _exercisesForm.value = _exercisesForm.value.map {
            if (it.id == id) it.copy(name = newName, sets = newSets, reps = newReps) else it
        }
        _formError.value = null
    }

    fun createRoutine(name: String, days: List<String>, onSuccess: () -> Unit) {
        // Validamos rápidamente la UI antes de armar el dominio
        if (name.isBlank() || days.isEmpty()) {
            _formError.value = "Ponle un nombre a la rutina y elige al menos un día."
            return
        }

        // Convertimos el estado visual a datos crudos para el Caso de Uso
        val rawExercises = _exercisesForm.value.mapNotNull {
            val setsInt = it.sets.toIntOrNull()
            val repsInt = it.reps.toIntOrNull()
            if (it.name.isNotBlank() && setsInt != null && repsInt != null) {
                Triple(it.name, setsInt, repsInt)
            } else null
        }

        viewModelScope.launch {
            val result = createRoutineUseCase(name, days, rawExercises)
            result.onSuccess {
                _formError.value = null
                // Reiniciamos el formulario
                _exercisesForm.value = listOf(ExerciseFormState())
                onSuccess()
            }.onFailure { exception ->
                _formError.value = exception.message
            }
        }
    }
}