package com.tlatoltech.gym.data.source.remote.dto

import com.google.gson.annotations.SerializedName
import com.tlatoltech.gym.domain.model.GymPlan

data class GymPlanDto(
    val id: Int?,
    val nombre: String,
    val nivel: String,
    val precio: Double,
    val activo: Boolean?
)

// Función de extensión para mapear el JSON técnico a nuestra Entidad Pura
fun GymPlanDto.toDomain(): GymPlan {
    return GymPlan(
        id = this.id,
        name = this.nombre,
        level = this.nivel,
        price = this.precio,
        isActive = this.activo ?: true
    )
}

// De Entidad a DTO para enviar a Laravel
fun GymPlan.toDto(): GymPlanDto {
    return GymPlanDto(
        id = this.id,
        nombre = this.name,
        nivel = this.level,
        precio = this.price,
        activo = this.isActive
    )
}