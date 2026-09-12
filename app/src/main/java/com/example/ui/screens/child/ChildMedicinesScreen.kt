package com.example.ui.screens.child

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Medicine
import com.example.data.repository.AppDataContainer
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import kotlinx.coroutines.launch

@Composable
fun ChildMedicinesScreen(
    onNavigateToAddMedicine: () -> Unit,
    onNavigateToEditMedicine: (String) -> Unit,
    onNavigateToDetails: (String) -> Unit,
    isParentView: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val allMedicines by AppDataContainer.medicineRepository.getMedicinesFlow().collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Active") } // Active, Archived, All
    var medicineToDelete by remember { mutableStateOf<Medicine?>(null) }

    val filteredMedicines = allMedicines.filter { med ->
        val matchesSearch = med.name.contains(searchQuery, ignoreCase = true) ||
                med.strength.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "Active" -> !med.isArchived
            "Archived" -> med.isArchived
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Scaffold(
        floatingActionButton = {
            if (!isParentView) {
                FloatingActionButton(
                    onClick = onNavigateToAddMedicine,
                    containerColor = TealPrimary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("add_medicine_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Medicine")
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header title
            Text(
                text = if (isParentView) "Arjun's Medicines" else "My Medicines",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isParentView) "Monitor prescribed medicines, dosages and schedules" else "Manage prescriptions, schedules, and reminders",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search medicine name or strength...") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("medicines_search_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Active", "Archived", "All").forEach { filter ->
                    FilterChip(
                        selected = (selectedFilter == filter),
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Medicines List
            if (filteredMedicines.isEmpty()) {
                EmptyStateView(
                    title = if (searchQuery.isNotBlank()) "No Matching Medicines" else "No Medicines Yet",
                    description = if (searchQuery.isNotBlank()) "Try changing your search keywords." else "Keep track of daily medications and schedules by adding your first medicine.",
                    actionLabel = if (!isParentView) "Add Medicine" else null,
                    onAction = onNavigateToAddMedicine
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredMedicines, key = { it.id }) { medicine ->
                        MedicineItemCard(
                            medicine = medicine,
                            onViewDetails = { onNavigateToDetails(medicine.id) },
                            onEdit = { onNavigateToEditMedicine(medicine.id) },
                            onArchiveToggle = {
                                scope.launch {
                                    AppDataContainer.medicineRepository.archiveMedicine(medicine.id, !medicine.isArchived)
                                }
                            },
                            onDelete = { medicineToDelete = medicine },
                            isParentView = isParentView
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    medicineToDelete?.let { med ->
        AlertDialog(
            onDismissRequest = { medicineToDelete = null },
            title = { Text("Delete ${med.name}?") },
            text = {
                Text("Are you sure you want to delete this medicine? All scheduled reminders and adherence logs for this medicine will be removed.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = med.id
                        medicineToDelete = null
                        scope.launch {
                            AppDataContainer.medicineRepository.deleteMedicine(id)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_medicine_button")
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { medicineToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MedicineItemCard(
    medicine: Medicine,
    onViewDetails: () -> Unit,
    onEdit: () -> Unit,
    onArchiveToggle: () -> Unit,
    onDelete: () -> Unit,
    isParentView: Boolean
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetails() }
            .testTag("medicine_item_${medicine.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (medicine.isArchived) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(TealContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = medicine.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (medicine.strength.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = medicine.strength,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${medicine.type.displayName} • ${medicine.dosage}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${medicine.frequency.displayName} (${medicine.reminderTimes.joinToString(", ") { it.formatted }})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // More Options Menu (Edit, Archive, Delete)
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("View Details") },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null) },
                        onClick = {
                            menuExpanded = false
                            onViewDetails()
                        }
                    )
                    if (!isParentView) {
                        DropdownMenuItem(
                            text = { Text("Edit Medicine") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (medicine.isArchived) "Unarchive" else "Archive") },
                            leadingIcon = {
                                Icon(
                                    if (medicine.isArchived) Icons.Default.Unarchive else Icons.Default.Archive,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onArchiveToggle()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}
