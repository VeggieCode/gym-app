package com.tlatoltech.gym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import com.tlatoltech.gym.data.source.local.GymDatabase
import com.tlatoltech.gym.data.source.remote.GymPlanApiService
import com.tlatoltech.gym.domain.usecase.ArchivePlanUseCase
import com.tlatoltech.gym.domain.usecase.CreatePlanUseCase
import com.tlatoltech.gym.domain.usecase.GetActiveGymPlansUseCase
import com.tlatoltech.gym.presentation.ui.ActivePlansScreen
import com.tlatoltech.gym.presentation.ui.AddPlanScreen
import com.tlatoltech.gym.presentation.viewmodel.GymPlanViewModel
import com.tlatoltech.gym.ui.theme.GymTheme
import retrofit2.Retrofit
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
        val baseUrl = "http://10.0.2.2/"

        // 1. Configuramos Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(GymPlanApiService::class.java)

        // 1. Crear BD de Room
        val database = Room.databaseBuilder(
            applicationContext,
            GymDatabase::class.java,
            "gym_database"
        ).build()

        // 2. Repositorio Real (ahora con Room y Retrofit)
        val repository = GymPlanRepositoryImpl(
            localDao = database.planDao(),
            apiService = apiService
        )

        // 3. Instanciamos los Casos de Uso
        val getActivePlansUseCase = GetActiveGymPlansUseCase(repository)
        val archivePlanUseCase = ArchivePlanUseCase(repository)
        val createPlanUseCase = CreatePlanUseCase(repository)

        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GymPlanViewModel(
                    getActivePlansUseCase,
                    archivePlanUseCase,
                    createPlanUseCase
                ) as T
            }
        }

        enableEdgeToEdge()
        setContent {
            val viewModel: GymPlanViewModel = viewModel(factory = factory)

            // Un sistema de navegación muy rústico para esta prueba
            var showAddScreen by remember { mutableStateOf(false) }

            if (showAddScreen) {
                AddPlanScreen(
                    viewModel = viewModel,
                    onNavigateBack = { showAddScreen = false }
                )
            } else {
                Column {
                    Button(
                        onClick = { showAddScreen = true },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Agregar Nuevo Plan")
                    }
                    ActivePlansScreen(viewModel = viewModel)
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