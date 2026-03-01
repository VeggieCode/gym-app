package com.tlatoltech.gym.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tlatoltech.gym.data.source.local.dao.PlanDao
import com.tlatoltech.gym.data.source.local.entity.PlanEntity

@Database(entities = [PlanEntity::class], version = 1, exportSchema = false)
abstract class GymDatabase : RoomDatabase() {
    abstract fun planDao(): PlanDao
}