package com.tlatoltech.gym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.tlatoltech.gym.data.repository.GymPlanRepositoryImpl
import com.tlatoltech.gym.data.repository.RoutineRepositoryImpl
import com.tlatoltech.gym.data.source.local.GymDatabase
import com.tlatoltech.gym.data.source.remote.GymPlanApiService
import com.tlatoltech.gym.data.source.remote.GymRoutineApiService
import com.tlatoltech.gym.domain.usecase.ArchivePlanUseCase
import com.tlatoltech.gym.domain.usecase.CreatePlanUseCase
import com.tlatoltech.gym.domain.usecase.CreateRoutineUseCase
import com.tlatoltech.gym.domain.usecase.GetActiveGymPlansUseCase
import com.tlatoltech.gym.presentation.ui.ActivePlansScreen
import com.tlatoltech.gym.presentation.ui.AddPlanScreen
import com.tlatoltech.gym.presentation.ui.AddRoutineScreen
import com.tlatoltech.gym.presentation.viewmodel.GymPlanViewModel
import com.tlatoltech.gym.presentation.viewmodel.RoutineViewModel
import com.tlatoltech.gym.ui.theme.GymTheme
import retrofit2.Retrofit
import androidx.compose.material3.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        // 1. Instanciamos nuestra infraestructura (El Mock)
//        val repository = MockGymPlanRepository();
//
//        // 2. Instanciamos los Casos de Uso pasándoles el repositorio
//        val getActivePlansUseCase = GetActiveGymPlansUseCase(repository)
//        val archivePlanUseCase = ArchiveGymPlanUseCase(repository)

        // IMPORTANTE SOBRE LA URL BASE:
        // Si pruebas en el Emulador de Android, "localhost" no funciona. Debes usar "10.0.2.2".
        // Si pruebas en un dispositivo físico, usa tu IP local (ej. "http://192.168.1.75:8000/")
        // 1. Configuración de Red (Retrofit)
        val baseUrl = "http://10.0.2.2/"

        // 1. Configuramos Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val planApiService = retrofit.create(GymPlanApiService::class.java)
        val routineApiService = retrofit.create(GymRoutineApiService::class.java)

        // 2. Configuración Local (Room)
        // Usamos fallbackToDestructiveMigration() solo para desarrollo,
        // para que borre y recree la BD sin pelear con migraciones manuales.
        val database = Room.databaseBuilder(
            applicationContext,
            GymDatabase::class.java,
            "gym_database"
        ).fallbackToDestructiveMigration().build()

        // 3. Repositorios (Orquestadores Offline-First)
        val planRepository = GymPlanRepositoryImpl(database.planDao(), planApiService)
        val routineRepository = RoutineRepositoryImpl(database.routineDao(), routineApiService)

        // 4. Casos de Uso (Reglas de Negocio)
        val getActivePlansUseCase = GetActiveGymPlansUseCase(planRepository)
        val archivePlanUseCase = ArchivePlanUseCase(planRepository)
        val createPlanUseCase = CreatePlanUseCase(planRepository)
        val createRoutineUseCase = CreateRoutineUseCase(routineRepository)

        // 5. Fábrica de ViewModels (Inyección Manual)
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(GymPlanViewModel::class.java)) {
                    return GymPlanViewModel(getActivePlansUseCase, archivePlanUseCase, createPlanUseCase) as T
                }
                if (modelClass.isAssignableFrom(RoutineViewModel::class.java)) {
                    return RoutineViewModel(createRoutineUseCase) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
//        enableEdgeToEdge()
        setContent {
            // Obtenemos las instancias de los ViewModels
            val planViewModel: GymPlanViewModel = viewModel(factory = factory)
            val routineViewModel: RoutineViewModel = viewModel(factory = factory)

            // Enrutador rudimentario para pruebas rápidas
            var currentScreen by remember { mutableStateOf("HOME") }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        "HOME" -> {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(onClick = { currentScreen = "ADD_PLAN" }) {
                                        Text("Nuevo Plan")
                                    }
                                    Button(onClick = { currentScreen = "ADD_ROUTINE" }) {
                                        Text("Nueva Rutina")
                                    }
                                }
                                // Mostramos la lista de planes por defecto
                                ActivePlansScreen(viewModel = planViewModel)
                            }
                        }
                        "ADD_PLAN" -> {
                            AddPlanScreen(
                                viewModel = planViewModel,
                                onNavigateBack = { currentScreen = "HOME" }
                            )
                        }
                        "ADD_ROUTINE" -> {
                            AddRoutineScreen(
                                viewModel = routineViewModel,
                                onNavigateBack = { currentScreen = "HOME" }
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GymTheme {
        Greeting("Android")
    }
}