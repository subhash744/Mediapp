package com.example.ui.screens.child

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DoseRecord
import com.example.data.model.DoseStatus
import com.example.data.repository.AppDataContainer
import com.example.ui.components.DoseCard
import com.example.ui.components.EmptyStateView
import kotlinx.coroutines.launch

@Composable
fun ChildRemindersScreen(
    onNavigateToMedicineDetails: (String) -> Unit,
    isParentView: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val todayDoses by AppDataContainer.doseRepository.getTodayDosesFlow().collectAsState(initial = emptyList())
    var selectedFilter by remember { mutableStateOf("All") } // All, Due Now, Upcoming, Completed

    val filteredDoses = todayDoses.filter { dose ->
        when (selectedFilter) {
            "Due Now" -> dose.status == DoseStatus.DUE
            "Upcoming" -> dose.status == DoseStatus.UPCOMING
            "Completed" -> dose.status == DoseStatus.TAKEN || dose.status == DoseStatus.SKIPPED
            else -> true
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isParentView) "Arjun's Reminders & Schedule" else "Daily Reminders",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Track doses that are due now, upcoming, or completed",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Due Now", "Upcoming", "Completed").forEach { filter ->
                    FilterChip(
                        selected = (selectedFilter == filter),
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredDoses.isEmpty()) {
                EmptyStateView(
                    title = "No $selectedFilter Reminders",
                    description = "There are no medicine reminders currently in the $selectedFilter list.",
                    actionLabel = if (selectedFilter != "All") "Show All Reminders" else null,
                    onAction = { selectedFilter = "All" }
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredDoses, key = { it.id }) { dose ->
                        DoseCard(
                            dose = dose,
                            onTake = {
                                scope.launch {
                                    AppDataContainer.doseRepository.recordTakeDose(dose.id)
                                }
                            },
                            onSkip = { reason ->
                                scope.launch {
                                    AppDataContainer.doseRepository.recordSkipDose(dose.id, reason)
                                }
                            },
                            onUndo = {
                                scope.launch {
                                    AppDataContainer.doseRepository.undoDoseAction(dose.id)
                                }
                            },
                            onViewMedicine = { onNavigateToMedicineDetails(dose.medicineId) },
                            isParentView = isParentView
                        )
                    }
                }
            }
        }
    }
}
