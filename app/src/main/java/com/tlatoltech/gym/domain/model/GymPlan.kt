package com.tlatoltech.gym.domain.model

import com.tlatoltech.gym.domain.exception.InvalidLevelException
import com.tlatoltech.gym.domain.exception.InvalidPriceException
import com.tlatoltech.gym.domain.exception.PlanAlreadyInactiveException

data class GymPlan(
    val id: Int?,
    val name: String,
    val level: String,
    val price: Double,
    val isActive: Boolean = true
) {
    init {
        // REGLA 1: Validación simple
        if (price < 0) {
            throw InvalidPriceException("El precio ($price) no puede ser negativo.")
        }

        // REGLA 2: Lista blanca
        val allowedLevels = listOf("Principiante", "Intermedio", "Avanzado")
        if (level !in allowedLevels) {
            throw InvalidLevelException("El nivel '$level' no existe en el gimnasio.")
        }

        // REGLA 3: Validación cruzada
        if (level == "Avanzado" && price < 50.00) {
            throw InvalidPriceException("Los planes avanzados son premium. Deben costar al menos $50.00.")
        }
    }

    // REGLA 4: Transición de estado (Comportamiento)
    fun archive(): GymPlan {
        if (!isActive) {
            throw PlanAlreadyInactiveException("Este plan ya fue archivado previamente.")
        }

        // Retornamos una nueva instancia idéntica, pero con isActive en false
        return this.copy(isActive = false)
    }
}
