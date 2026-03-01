package com.tlatoltech.gym.presentation.ui


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tlatoltech.gym.presentation.viewmodel.GymPlanViewModel

@Composable
fun AddPlanScreen(
    viewModel: GymPlanViewModel,
    onNavigateBack: () -> Unit // Función para regresar a la lista
) {
    // Estados locales para los campos de texto
    var name by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("Principiante") }
    var price by remember { mutableStateOf("") }

    // Observamos el error del dominio que nos manda el ViewModel
    val formError by viewModel.formError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Crear Nuevo Plan", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                viewModel.clearFormError()
            },
            label = { Text("Nombre del Plan") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = level,
            onValueChange = {
                level = it
                viewModel.clearFormError()
            },
            label = { Text("Nivel (Principiante, Intermedio, Avanzado)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = price,
            onValueChange = {
                price = it
                viewModel.clearFormError()
            },
            label = { Text("Precio") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Si la entidad pura lanza un error, lo pintamos aquí en rojo
        if (formError != null) {
            Text(
                text = formError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onNavigateBack) {
                Text("Cancelar")
            }

            Button(onClick = {
                viewModel.createPlan(
                    name = name,
                    level = level,
                    priceStr = price,
                    onSuccess = onNavigateBack
                )
            }) {
                Text("Guardar Plan")
            }
        }
    }
}