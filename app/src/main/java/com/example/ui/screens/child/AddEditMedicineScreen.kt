package com.example.ui.screens.child

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodTiming
import com.example.data.model.Frequency
import com.example.data.model.Medicine
import com.example.data.model.MedicineType
import com.example.data.model.ReminderTime
import com.example.data.repository.AppDataContainer
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditMedicineScreen(
    medicineId: String? = null,
    onSaveSuccess: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val isEditing = medicineId != null

    var name by remember { mutableStateOf("") }
    var strength by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("1 tablet") }
    var selectedType by remember { mutableStateOf(MedicineType.TABLET) }
    var selectedFrequency by remember { mutableStateOf(Frequency.DAILY) }
    var timesPerDay by remember { mutableIntStateOf(1) }
    var reminderTimes by remember {
        mutableStateOf(listOf(ReminderTime(hour = 8, minute = 0)))
    }
    var isOngoing by remember { mutableStateOf(true) }
    var selectedFoodTiming by remember { mutableStateOf(FoodTiming.AFTER_FOOD) }
    var notes by remember { mutableStateOf("") }
    var refillQuantity by remember { mutableStateOf("30") }
    var refillThreshold by remember { mutableStateOf("5") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showConfirmationSummary by remember { mutableStateOf(false) }

    // Prepopulate when editing
    LaunchedEffect(medicineId) {
        if (medicineId != null) {
            val med = AppDataContainer.medicineRepository.getMedicineById(medicineId)
            if (med != null) {
                name = med.name
                strength = med.strength
                dosage = med.dosage
                selectedType = med.type
                selectedFrequency = med.frequency
                timesPerDay = med.timesPerDay
                reminderTimes = med.reminderTimes
                isOngoing = med.isOngoing
                selectedFoodTiming = med.foodTiming
                notes = med.notes
                refillQuantity = med.totalQuantity.toString()
                refillThreshold = med.refillThreshold.toString()
            }
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("add_medicine_back")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditing) "Edit Medicine" else "Add New Medicine",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 1. Medicine Name (Required)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; errorMessage = null },
                label = { Text("Medicine Name *") },
                placeholder = { Text("e.g., Amoxicillin, Ibuprofen") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("medicine_name_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Strength & Dosage
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = strength,
                    onValueChange = { strength = it },
                    label = { Text("Strength") },
                    placeholder = { Text("e.g. 500 mg") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("medicine_strength_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage") },
                    placeholder = { Text("e.g. 1 capsule") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("medicine_dosage_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Medicine Type Chips
            Text("Medicine Type", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MedicineType.values().forEach { type ->
                    FilterChip(
                        selected = (selectedType == type),
                        onClick = { selectedType = type },
                        label = { Text(type.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Frequency & Times Per Day
            Text("Frequency", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Frequency.values().forEach { freq ->
                    FilterChip(
                        selected = (selectedFrequency == freq),
                        onClick = { selectedFrequency = freq },
                        label = { Text(freq.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Specific Reminder Times (Allows multiple!)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reminder Times (${reminderTimes.size} per day)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                TextButton(
                    onClick = {
                        val nextHour = when (reminderTimes.size) {
                            1 -> 14 // 2:00 PM
                            2 -> 20 // 8:00 PM
                            3 -> 12 // 12:00 PM
                            else -> (reminderTimes.last().hour + 4) % 24
                        }
                        reminderTimes = reminderTimes + ReminderTime(hour = nextHour, minute = 0)
                        timesPerDay = reminderTimes.size
                    },
                    modifier = Modifier.testTag("add_reminder_time_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Time")
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                reminderTimes.forEachIndexed { index, time ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(TealContainer)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = time.formatted, fontWeight = FontWeight.Bold, color = TealPrimary, fontSize = 13.sp)
                            if (reminderTimes.size > 1) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = TealPrimary,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable {
                                            reminderTimes = reminderTimes.filterIndexed { i, _ -> i != index }
                                            timesPerDay = reminderTimes.size
                                        }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Food Relationship
            Text("Food Instructions", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FoodTiming.values().forEach { timing ->
                    FilterChip(
                        selected = (selectedFoodTiming == timing),
                        onClick = { selectedFoodTiming = timing },
                        label = { Text(timing.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7. Ongoing Prescription Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Ongoing Prescription", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("Remind indefinitely without end date", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = isOngoing,
                    onCheckedChange = { isOngoing = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 8. Refill Details
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = refillQuantity,
                    onValueChange = { refillQuantity = it },
                    label = { Text("Starting Stock / Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = refillThreshold,
                    onValueChange = { refillThreshold = it },
                    label = { Text("Low Stock Alert Level") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 9. Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Instructions & Notes (Optional)") },
                placeholder = { Text("e.g., Take with full glass of water, finish all pills") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Action Button (validates and triggers confirmation dialog)
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Please enter a medicine name."
                        return@Button
                    }
                    if (reminderTimes.isEmpty()) {
                        errorMessage = "Please add at least one reminder time."
                        return@Button
                    }
                    showConfirmationSummary = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_medicine_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text(
                    text = if (isEditing) "Save Changes" else "Review & Save Medicine",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Confirmation Summary Dialog before saving
    if (showConfirmationSummary) {
        AlertDialog(
            onDismissRequest = { showConfirmationSummary = false },
            title = {
                Text(if (isEditing) "Confirm Changes" else "Confirm New Medicine")
            },
            text = {
                Column {
                    Text(
                        text = "Please verify the schedule summary before saving:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "• Medicine: $name ${if (strength.isNotBlank()) "($strength)" else ""}", fontWeight = FontWeight.Bold)
                            Text(text = "• Dosage: $dosage • ${selectedType.displayName}")
                            Text(text = "• Timing: ${selectedFoodTiming.displayName}")
                            Text(text = "• Schedule: ${reminderTimes.joinToString(", ") { it.formatted }}")
                            Text(text = "• Stock: ${refillQuantity.toIntOrNull() ?: 30} units (Alert at ${refillThreshold.toIntOrNull() ?: 5})")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmationSummary = false
                        val finalStock = refillQuantity.toIntOrNull() ?: 30
                        val finalThreshold = refillThreshold.toIntOrNull() ?: 5
                        val med = Medicine(
                            id = medicineId ?: UUID.randomUUID().toString(),
                            name = name.trim(),
                            strength = strength.trim(),
                            dosage = dosage.trim(),
                            type = selectedType,
                            frequency = selectedFrequency,
                            timesPerDay = reminderTimes.size,
                            reminderTimes = reminderTimes,
                            isOngoing = isOngoing,
                            foodTiming = selectedFoodTiming,
                            notes = notes.trim(),
                            totalQuantity = finalStock,
                            currentStock = finalStock,
                            refillThreshold = finalThreshold
                        )
                        scope.launch {
                            if (isEditing) {
                                AppDataContainer.medicineRepository.updateMedicine(med)
                            } else {
                                AppDataContainer.medicineRepository.addMedicine(med)
                                AppDataContainer.onMedicineAdded(med)
                            }
                            onSaveSuccess()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    modifier = Modifier.testTag("confirm_save_summary_button")
                ) {
                    Text("Confirm & Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationSummary = false }) {
                    Text("Edit")
                }
            }
        )
    }
}
