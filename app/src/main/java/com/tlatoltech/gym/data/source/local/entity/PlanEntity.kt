package com.tlatoltech.gym.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// El nombre de la tabla en SQLite local
@Entity(tableName = "planes")
data class PlanEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0, // ID autogenerado localmente
    val remoteId: Int?,   // El ID real de Laravel (puede ser nulo si no se ha sincronizado)
    val name: String,
    val level: String,
    val price: Double,
    val isActive: Boolean,
    val syncStatus: String // Puede ser "SYNCED", "PENDING_CREATE", "PENDING_UPDATE"
)