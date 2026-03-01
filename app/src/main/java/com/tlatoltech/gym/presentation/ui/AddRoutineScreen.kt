package com.tlatoltech.gym.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tlatoltech.gym.presentation.viewmodel.RoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoutineScreen(
    viewModel: RoutineViewModel,
    onNavigateBack: () -> Unit
) {
    var routineName by remember { mutableStateOf("") }
    // Para simplificar la prueba, fijaremos los días, pero en un caso real usarías Checkboxes
    val selectedDays = listOf("Lunes", "Miércoles", "Viernes")

    val exercises by viewModel.exercisesForm.collectAsState()
    val error by viewModel.formError.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nueva Rutina (Offline-First)") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.addExerciseField() },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Añadir Ejercicio") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            OutlinedTextField(
                value = routineName,
                onValueChange = { routineName = it },
                label = { Text("Nombre de la Rutina (Ej. Fuerza A)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Días: ${selectedDays.joinToString()}", modifier = Modifier.padding(vertical = 8.dp))

            if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Ejercicios:", style = MaterialTheme.typography.titleMedium)

            // Lista dinámica de ejercicios
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(exercises) { exercise ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = exercise.name,
                                    onValueChange = { viewModel.updateExerciseField(exercise.id, it, exercise.sets, exercise.reps) },
                                    label = { Text("Ejercicio") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = exercise.sets,
                                        onValueChange = { viewModel.updateExerciseField(exercise.id, exercise.name, it, exercise.reps) },
                                        label = { Text("Series") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = exercise.reps,
                                        onValueChange = { viewModel.updateExerciseField(exercise.id, exercise.name, exercise.sets, it) },
                                        label = { Text("Reps") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            IconButton(onClick = { viewModel.removeExerciseField(exercise.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(onClick = onNavigateBack) { Text("Cancelar") }
                Button(onClick = { viewModel.createRoutine(routineName, selectedDays, onNavigateBack) }) {
                    Text("Guardar Transacción")
                }
            }
        }
    }
}