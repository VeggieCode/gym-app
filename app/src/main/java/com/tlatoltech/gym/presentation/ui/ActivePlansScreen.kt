package com.tlatoltech.gym.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tlatoltech.gym.domain.model.GymPlan
import com.tlatoltech.gym.presentation.state.GymPlanUiState
import com.tlatoltech.gym.presentation.viewmodel.GymPlanViewModel

@Composable
fun ActivePlansScreen(
    viewModel: GymPlanViewModel
) {
    // Observamos el estado. Cada vez que cambie, la pantalla se recompondrá automáticamente.
    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is GymPlanUiState.Loading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }
            is GymPlanUiState.Error -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
            is GymPlanUiState.Success -> {
                PlanList(
                    gymPlans = state.gymPlans,
                    onArchiveClick = { planId -> viewModel.archivePlan(planId) }
                )
            }
        }
    }
}

@Composable
fun PlanList(gymPlans: List<GymPlan>, onArchiveClick: (String) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(gymPlans) { plan ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = plan.name, style = MaterialTheme.typography.titleLarge)
                    Text(text = plan.level, style = MaterialTheme.typography.bodyMedium)
                    Text(text = "$${plan.price}", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = { onArchiveClick(plan.id.toString()) }) {
                        Text("Archivar Plan")
                    }
                }
            }
        }
    }
}