package com.tlatoltech.gym.data.mapper

import com.tlatoltech.gym.data.source.local.entity.PlanEntity
import com.tlatoltech.gym.data.source.remote.dto.GymPlanDto
import com.tlatoltech.gym.domain.model.GymPlan


fun PlanEntity.toDomain(): GymPlan {
    return GymPlan(
        id = this.remoteId ?: this.localId, // Usamos el ID remoto si existe, sino el local temporal
        name = this.name,
        level = this.level,
        price = this.price,
        isActive = this.isActive
    )
}

fun GymPlan.toLocalEntity(syncStatus: String = "SYNCED"): PlanEntity {
    return PlanEntity(
        localId = if (this.id != null && syncStatus == "PENDING_CREATE") 0 else this.id ?: 0,
        remoteId = if (syncStatus == "PENDING_CREATE") null else this.id,
        name = this.name,
        level = this.level,
        price = this.price,
        isActive = this.isActive,
        syncStatus = syncStatus
    )
}