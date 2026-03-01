package com.tlatoltech.gym

import android.app.Application
import androidx.room.Room
import com.tlatoltech.gym.data.repository.GymPlanRepositoryImpl
import com.tlatoltech.gym.data.repository.RoutineRepositoryImpl
import com.tlatoltech.gym.data.source.local.GymDatabase
import com.tlatoltech.gym.data.source.remote.GymPlanApiService
import com.tlatoltech.gym.data.source.remote.GymRoutineApiService
import com.tlatoltech.gym.domain.repository.GymPlanRepository
import com.tlatoltech.gym.domain.repository.RoutineRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GymApplication : Application() {

    // Nuestro contenedor de inyección de dependencias manual
    // Contenedor de dependencias centralizado
    lateinit var routineRepository: RoutineRepository
    lateinit var planRepository: GymPlanRepository

    override fun onCreate() {
        super.onCreate()

        // IMPORTANTE SOBRE LA URL BASE:
        // Si pruebas en el Emulador de Android, "localhost" no funciona. Debes usar "10.0.2.2".
        // Si pruebas en un dispositivo físico, usa tu IP local (ej. "http://192.168.1.75:8000/")
        // 1. Configuración de Red (Retrofit)
        val baseUrl = "http://10.0.2.2/"

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val routineApiService = retrofit.create(GymRoutineApiService::class.java)
        val planApiService = retrofit.create(GymPlanApiService::class.java)

        val database = Room.databaseBuilder(
            this,
            GymDatabase::class.java,
            "gym_database"
        ).fallbackToDestructiveMigration().build()

        routineRepository = RoutineRepositoryImpl(database.routineDao(), routineApiService)
        planRepository = GymPlanRepositoryImpl(database.planDao(), planApiService)
    }
}