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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectionStatus
import com.example.data.model.DoseRecord
import com.example.data.model.DoseStatus
import com.example.data.repository.AppDataContainer
import com.example.ui.components.AdherenceCard
import com.example.ui.components.DoseCard
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.SapphireContainer
import com.example.ui.theme.SapphirePrimary
import com.example.ui.theme.StatusDue
import com.example.ui.theme.StatusDueContainer
import com.example.ui.theme.StatusTaken
import com.example.ui.theme.StatusTakenContainer
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChildHomeScreen(
    onNavigateToAddMedicine: () -> Unit,
    onNavigateToMedicineDetails: (String) -> Unit,
    onNavigateToMedicinesList: () -> Unit,
    onNavigateToConnectedFamily: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val todayDoses by AppDataContainer.doseRepository.getTodayDosesFlow().collectAsState(initial = emptyList())
    val adherenceStats by AppDataContainer.doseRepository.getAdherenceStatsFlow().collectAsState(
        initial = com.example.data.model.AdherenceStats(5, 1, 0, 0, 0.2f, 5)
    )
    val connectionStatus by AppDataContainer.familyConnectionRepository.connectionStatus.collectAsState()
    val connectedParent by AppDataContainer.familyConnectionRepository.connectedParent.collectAsState()

    val todayDateFormatted = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())

    // Next dose computation
    val nextDose = todayDoses.firstOrNull { it.status == DoseStatus.DUE }
        ?: todayDoses.firstOrNull { it.status == DoseStatus.UPCOMING }

    val takenCount = todayDoses.count { it.status == DoseStatus.TAKEN }
    val dueCount = todayDoses.count { it.status == DoseStatus.DUE }
    val upcomingCount = todayDoses.count { it.status == DoseStatus.UPCOMING }
    val skippedCount = todayDoses.count { it.status == DoseStatus.SKIPPED }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddMedicine,
                containerColor = TealPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("child_add_medicine_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Medicine")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Greeting & Today's Date
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Good Morning, Arjun 👋",
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
                            .background(TealContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Medication,
                            contentDescription = "Child Avatar",
                            tint = TealPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // 2. Connected Parent Status Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToConnectedFamily() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (connectionStatus == ConnectionStatus.CONNECTED) SapphireContainer else TealContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SupervisorAccount,
                                contentDescription = null,
                                tint = if (connectionStatus == ConnectionStatus.CONNECTED) SapphirePrimary else TealPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (connectionStatus == ConnectionStatus.CONNECTED) {
                                        "Monitored by Parent (${connectedParent?.name ?: "Rahul"})"
                                    } else {
                                        "Parent Not Connected"
                                    },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (connectionStatus == ConnectionStatus.CONNECTED) SapphirePrimary else TealPrimary
                                )
                                Text(
                                    text = if (connectionStatus == ConnectionStatus.CONNECTED) {
                                        "Schedules and adherence are synced"
                                    } else {
                                        "Tap to share pairing code with your parent"
                                    },
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "View connection",
                            tint = if (connectionStatus == ConnectionStatus.CONNECTED) SapphirePrimary else TealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // 3. Highlighted Next Medicine Card (Hero Section)
            if (nextDose != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (nextDose.status == DoseStatus.DUE) StatusDueContainer else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (nextDose.status == DoseStatus.DUE) Icons.Default.NotificationsActive else Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = if (nextDose.status == DoseStatus.DUE) StatusDue else TealPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (nextDose.status == DoseStatus.DUE) "NEXT MEDICINE DUE NOW" else "NEXT UPCOMING DOSE",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        letterSpacing = 1.sp,
                                        color = if (nextDose.status == DoseStatus.DUE) Color(0xFFB45309) else TealPrimary
                                    )
                                }

                                Text(
                                    text = nextDose.scheduledTime.formatted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = nextDose.medicineName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${nextDose.dosage} • ${nextDose.foodTiming.displayName}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            AppDataContainer.doseRepository.recordTakeDose(nextDose.id)
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .height(46.dp)
                                        .testTag("hero_take_button"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (nextDose.status == DoseStatus.DUE) StatusDue else TealPrimary
                                    )
                                ) {
                                    Text(
                                        text = if (nextDose.status == DoseStatus.DUE) "Take Now" else "Take Early",
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Button(
                                    onClick = { onNavigateToMedicineDetails(nextDose.medicineId) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors()
                                ) {
                                    Text("Details")
                                }
                            }
                        }
                    }
                }
            }

            // 4. Quick Today Summary Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryChip(
                        label = "Taken",
                        count = takenCount,
                        color = StatusTaken,
                        bgColor = StatusTakenContainer,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryChip(
                        label = "Due",
                        count = dueCount,
                        color = StatusDue,
                        bgColor = StatusDueContainer,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryChip(
                        label = "Upcoming",
                        count = upcomingCount,
                        color = MaterialTheme.colorScheme.primary,
                        bgColor = TealContainer,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryChip(
                        label = "Skipped",
                        count = skippedCount,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        bgColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 5. Adherence Progress Card
            item {
                AdherenceCard(
                    stats = adherenceStats,
                    title = "Today's Adherence",
                    subtitle = "$takenCount of ${todayDoses.size} doses completed today"
                )
            }

            // 6. Section Header: Medicines Due Today
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Medicines Due Today",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onNavigateToMedicinesList) {
                        Text("View All (${todayDoses.size})", fontSize = 13.sp)
                    }
                }
            }

            // 7. Today's Dose List (Empty State or Dose Cards)
            if (todayDoses.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No Medicines Due Today",
                        description = "You don't have any medicine doses scheduled for today. Add a medicine or reset the sample data to test!",
                        actionLabel = "Add Medicine",
                        onAction = onNavigateToAddMedicine
                    )
                }
            } else {
                items(todayDoses, key = { it.id }) { dose ->
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
                        onViewMedicine = { onNavigateToMedicineDetails(dose.medicineId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryChip(
    label: String,
    count: Int,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = count.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}
