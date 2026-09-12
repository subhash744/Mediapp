package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdherenceStats
import com.example.ui.theme.StatusMissed
import com.example.ui.theme.StatusMissedContainer
import com.example.ui.theme.StatusSkipped
import com.example.ui.theme.StatusSkippedContainer
import com.example.ui.theme.StatusTaken
import com.example.ui.theme.StatusTakenContainer
import com.example.ui.theme.TealPrimary

@Composable
fun AdherenceCard(
    stats: AdherenceStats,
    title: String = "Medicine Adherence",
    subtitle: String = "Overall compliance score",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Streak Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFEF3C7))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${stats.streakDays} Day Streak",
                            color = Color(0xFFB45309),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ring + Main Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(72.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { stats.adherenceRate },
                        modifier = Modifier.size(72.dp),
                        color = TealPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 7.dp,
                    )
                    val percent = (stats.adherenceRate * 100).toInt()
                    Text(
                        text = "$percent%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Stats breakdown
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MiniStatItem(
                        label = "Taken",
                        count = stats.takenDoses,
                        color = StatusTaken,
                        bgColor = StatusTakenContainer
                    )
                    MiniStatItem(
                        label = "Skipped",
                        count = stats.skippedDoses,
                        color = StatusSkipped,
                        bgColor = StatusSkippedContainer
                    )
                    MiniStatItem(
                        label = "Missed",
                        count = stats.missedDoses,
                        color = StatusMissed,
                        bgColor = StatusMissedContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniStatItem(
    label: String,
    count: Int,
    color: Color,
    bgColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = count.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}
