package com.tlatoltech.gym.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val remoteId: Int?,
    val name: String,
    val assignedDays: List<String>, // Magia del TypeConverter
    val syncStatus: String
)