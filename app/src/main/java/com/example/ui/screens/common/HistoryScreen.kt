package com.example.ui.screens.common

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DoseStatus
import com.example.data.model.HistoryLog
import com.example.data.repository.AppDataContainer
import com.example.ui.components.AdherenceCard
import com.example.ui.components.DoseStatusBadge
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    isParentView: Boolean = false,
    modifier: Modifier = Modifier
) {
    val historyLogs by AppDataContainer.doseRepository.getHistoryLogsFlow().collectAsState(initial = emptyList())
    val stats by AppDataContainer.doseRepository.getAdherenceStatsFlow().collectAsState(
        initial = com.example.data.model.AdherenceStats(14, 2, 1, 0, 0.82f, 5)
    )

    var selectedStatusFilter by remember { mutableStateOf("All") } // All, Taken, Skipped, Missed

    val filteredLogs = historyLogs.filter { log ->
        when (selectedStatusFilter) {
            "Taken" -> log.status == DoseStatus.TAKEN
            "Skipped" -> log.status == DoseStatus.SKIPPED
            "Missed" -> log.status == DoseStatus.MISSED
            else -> true
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Text(
                    text = if (isParentView) "Arjun's Medicine History" else "Dose History & Logs",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isParentView) "Complete log of taken, skipped, and missed medications" else "Track past doses, compliance, and notes",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Adherence summary card
            item {
                AdherenceCard(
                    stats = stats,
                    title = "Historical Adherence",
                    subtitle = "Based on the last 30 days of recorded doses"
                )
            }

            // Filter chips
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Taken", "Skipped", "Missed").forEach { status ->
                        FilterChip(
                            selected = (selectedStatusFilter == status),
                            onClick = { selectedStatusFilter = status },
                            label = { Text(status) }
                        )
                    }
                }
            }

            // Log entries
            if (filteredLogs.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No History Logs Found",
                        description = "There are no recorded medication doses matching the selected filter.",
                        icon = Icons.Default.History
                    )
                }
            } else {
                items(filteredLogs, key = { it.id }) { log ->
                    HistoryLogCard(log = log)
                }
            }
        }
    }
}

@Composable
private fun HistoryLogCard(
    log: HistoryLog,
    modifier: Modifier = Modifier
) {
    val dateFormatted = SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault()).format(Date(log.recordedAt))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("history_log_${log.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(TealContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = log.medicineName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    DoseStatusBadge(status = log.status)
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${log.dosage} • ${log.scheduledTime.formatted}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = dateFormatted,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                if (log.note != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Note: ${log.note}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
