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

        // 1. Obtenemos las dependencias desde nuestro Contenedor de Aplicación
        val app = application as GymApplication
        val planRepository = app.planRepository
        val routineRepository = app.routineRepository

        // 2. Casos de Uso (Reglas de Negocio)
        val getActivePlansUseCase = GetActiveGymPlansUseCase(planRepository)
        val archivePlanUseCase = ArchivePlanUseCase(planRepository)
        val createPlanUseCase = CreatePlanUseCase(planRepository)

        val createRoutineUseCase = CreateRoutineUseCase(routineRepository)

        // 3. Fábrica de ViewModels
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(GymPlanViewModel::class.java)) {
                    return GymPlanViewModel(getActivePlansUseCase, archivePlanUseCase, createPlanUseCase) as T
                }
                if (modelClass.isAssignableFrom(RoutineViewModel::class.java)) {
                    return RoutineViewModel(app, createRoutineUseCase) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
//        enableEdgeToEdge()
        setContent {
            val planViewModel: GymPlanViewModel = viewModel(factory = factory)
            val routineViewModel: RoutineViewModel = viewModel(factory = factory)

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