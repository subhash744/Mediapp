package com.example.ui.screens.parent

import android.widget.Toast
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
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectionStatus
import com.example.data.model.DoseRecord
import com.example.data.model.DoseStatus
import com.example.data.model.HistoryLog
import com.example.data.repository.AppDataContainer
import com.example.ui.components.AdherenceCard
import com.example.ui.components.DoseCard
import com.example.ui.components.DoseStatusBadge
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.SapphireContainer
import com.example.ui.theme.SapphirePrimary
import com.example.ui.theme.StatusDue
import com.example.ui.theme.StatusDueContainer
import com.example.ui.theme.StatusMissed
import com.example.ui.theme.StatusMissedContainer
import com.example.ui.theme.StatusTaken
import com.example.ui.theme.StatusTakenContainer
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ParentHomeScreen(
    onNavigateToMedicines: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToMedicineDetails: (String) -> Unit,
    onNavigateToPairing: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val todayDoses by AppDataContainer.doseRepository.getTodayDosesFlow().collectAsState(initial = emptyList())
    val adherenceStats by AppDataContainer.doseRepository.getAdherenceStatsFlow().collectAsState(
        initial = com.example.data.model.AdherenceStats(5, 1, 0, 0, 0.2f, 5)
    )
    val historyLogs by AppDataContainer.doseRepository.getHistoryLogsFlow().collectAsState(initial = emptyList())
    val connectionStatus by AppDataContainer.familyConnectionRepository.connectionStatus.collectAsState()
    val connectedChild by AppDataContainer.familyConnectionRepository.connectedChild.collectAsState()

    val pendingDoses = todayDoses.filter { it.status == DoseStatus.DUE || it.status == DoseStatus.UPCOMING }
    val completedDoses = todayDoses.filter { it.status == DoseStatus.TAKEN || it.status == DoseStatus.SKIPPED }
    val missedDoses = todayDoses.filter { it.status == DoseStatus.MISSED }

    val todayDateFormatted = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header Greeting & Date
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hello, Rahul 👋",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = todayDateFormatted,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SapphireContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupervisorAccount,
                            contentDescription = "Parent Avatar",
                            tint = SapphirePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // 2. Monitored Child Selector / Profile Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPairing() }
                        .testTag("monitored_child_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(TealContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ChildCare, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(28.dp))
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = connectedChild?.name ?: "Arjun",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(StatusTakenContainer)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Active & Synced",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusTaken
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Age 12 • 3 Prescriptions active",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Manage",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 3. Missed Dose Alert (if any)
            if (missedDoses.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = StatusMissedContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = StatusMissed, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Missed Dose Alert",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF991B1B)
                                )
                                Text(
                                    text = "${missedDoses.first().medicineName} scheduled for ${missedDoses.first().scheduledTime.formatted} was missed.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF7F1D1D)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Today's Adherence Score
            item {
                AdherenceCard(
                    stats = adherenceStats,
                    title = "Arjun's Adherence Score",
                    subtitle = "Real-time compliance calculation"
                )
            }

            // 5. Quick Actions Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Send Reminder Nudge
                    Button(
                        onClick = {
                            Toast.makeText(context, "Reminder notification sent to Arjun's device 🔔", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("parent_nudge_reminder_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SapphirePrimary)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Remind Child", fontSize = 12.sp)
                    }

                    // View Full Schedule
                    OutlinedButton(
                        onClick = onNavigateToMedicines,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Medication, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("All Medicines", fontSize = 12.sp)
                    }
                }
            }

            // 6. Section: Today's Pending Doses
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pending Doses Today (${pendingDoses.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (pendingDoses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = StatusTakenContainer)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusTaken)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "All scheduled doses for today are complete! Great job Arjun.",
                                fontSize = 13.sp,
                                color = Color(0xFF065F46)
                            )
                        }
                    }
                }
            } else {
                items(pendingDoses, key = { it.id }) { dose ->
                    DoseCard(
                        dose = dose,
                        onTake = { },
                        onSkip = { },
                        onUndo = { },
                        onViewMedicine = { onNavigateToMedicineDetails(dose.medicineId) },
                        isParentView = true
                    )
                }
            }

            // 7. Activity Stream (Recent dose events)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Activity Stream",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onNavigateToHistory) {
                        Text("View All History", fontSize = 13.sp)
                    }
                }
            }

            items(historyLogs.take(4)) { log ->
                val timeStr = SimpleDateFormat("h:mm a • MMM d", Locale.getDefault()).format(Date(log.recordedAt))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    when (log.status) {
                                        DoseStatus.TAKEN -> StatusTakenContainer
                                        DoseStatus.SKIPPED -> MaterialTheme.colorScheme.surfaceVariant
                                        else -> StatusMissedContainer
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (log.status) {
                                    DoseStatus.TAKEN -> Icons.Default.CheckCircle
                                    DoseStatus.SKIPPED -> Icons.Default.NotificationsActive
                                    else -> Icons.Default.Warning
                                },
                                contentDescription = null,
                                tint = when (log.status) {
                                    DoseStatus.TAKEN -> StatusTaken
                                    DoseStatus.SKIPPED -> MaterialTheme.colorScheme.onSurfaceVariant
                                    else -> StatusMissed
                                },
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Arjun ${if (log.status == DoseStatus.TAKEN) "took" else "skipped"} ${log.medicineName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "$timeStr ${if (log.note != null) "• ${log.note}" else ""}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        DoseStatusBadge(status = log.status)
                    }
                }
            }
        }
    }
}
