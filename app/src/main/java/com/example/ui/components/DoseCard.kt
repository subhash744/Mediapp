package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DoseRecord
import com.example.data.model.DoseStatus
import com.example.data.model.MedicineType
import com.example.ui.theme.StatusDue
import com.example.ui.theme.StatusDueContainer
import com.example.ui.theme.StatusTaken
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DoseCard(
    dose: DoseRecord,
    onTake: () -> Unit,
    onSkip: (reason: String) -> Unit,
    onUndo: () -> Unit,
    onViewMedicine: () -> Unit,
    isParentView: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showSkipDialog by remember { mutableStateOf(false) }
    var selectedSkipReason by remember { mutableStateOf("Feeling better") }

    val skipReasons = listOf(
        "Feeling better",
        "Experiencing side effects",
        "Out of medicine / Refill needed",
        "Doctor instructed to pause",
        "Missed schedule window"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewMedicine() }
            .testTag("dose_card_${dose.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (dose.status == DoseStatus.DUE) {
                StatusDueContainer.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (dose.status == DoseStatus.DUE) 2.dp else 1.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Time & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Schedule Time",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = dose.scheduledTime.formatted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                DoseStatusBadge(status = dose.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Middle Row: Medicine details & Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(TealContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = "Medicine",
                        tint = TealPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = dose.medicineName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (dose.medicineStrength.isNotBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${dose.medicineStrength}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = dose.dosage,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = "Food instruction",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = dose.foodTiming.displayName,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View Details",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Status message or Action Buttons
            Spacer(modifier = Modifier.height(14.dp))

            when (dose.status) {
                DoseStatus.TAKEN -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val timeStr = dose.takenAt?.let {
                            SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(it))
                        } ?: "Recorded"
                        Text(
                            text = "✓ Taken at $timeStr",
                            color = StatusTaken,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (!isParentView) {
                            TextButton(
                                onClick = onUndo,
                                modifier = Modifier.testTag("undo_dose_${dose.id}")
                            ) {
                                Icon(Icons.Default.Undo, contentDescription = "Undo", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Undo", fontSize = 12.sp)
                            }
                        }
                    }
                }
                DoseStatus.SKIPPED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Skipped (${dose.skippedReason ?: "No reason given"})",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                        if (!isParentView) {
                            TextButton(onClick = onUndo) {
                                Icon(Icons.Default.Undo, contentDescription = "Undo", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Undo", fontSize = 12.sp)
                            }
                        }
                    }
                }
                DoseStatus.DUE, DoseStatus.UPCOMING -> {
                    if (!isParentView) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showSkipDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("skip_button_${dose.id}"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Skip", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Skip")
                            }

                            Button(
                                onClick = onTake,
                                modifier = Modifier
                                    .weight(1.4f)
                                    .testTag("take_button_${dose.id}"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (dose.status == DoseStatus.DUE) StatusDue else TealPrimary
                                )
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Take", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (dose.status == DoseStatus.DUE) "Take Now" else "Take Dose",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Text(
                            text = if (dose.status == DoseStatus.DUE) "• Dose is currently due for Arjun" else "• Scheduled for later today",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                DoseStatus.MISSED -> {
                    Text(
                        text = "⚠ Missed dose window",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // Skip Confirmation Dialog
    if (showSkipDialog) {
        AlertDialog(
            onDismissRequest = { showSkipDialog = false },
            title = {
                Text(text = "Skip ${dose.medicineName}?")
            },
            text = {
                Column {
                    Text(
                        text = "Are you sure you want to skip this scheduled dose? Please select a reason for your parent/guardian to see:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    skipReasons.forEach { reason ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedSkipReason = reason }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (selectedSkipReason == reason),
                                onClick = { selectedSkipReason = reason }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = reason, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSkipDialog = false
                        onSkip(selectedSkipReason)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_skip_button")
                ) {
                    Text("Confirm Skip")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSkipDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
