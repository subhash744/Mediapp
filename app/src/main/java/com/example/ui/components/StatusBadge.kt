package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
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
import com.example.data.model.DoseStatus
import com.example.ui.theme.StatusDue
import com.example.ui.theme.StatusDueContainer
import com.example.ui.theme.StatusMissed
import com.example.ui.theme.StatusMissedContainer
import com.example.ui.theme.StatusSkipped
import com.example.ui.theme.StatusSkippedContainer
import com.example.ui.theme.StatusTaken
import com.example.ui.theme.StatusTakenContainer
import com.example.ui.theme.StatusUpcoming
import com.example.ui.theme.StatusUpcomingContainer

@Composable
fun DoseStatusBadge(
    status: DoseStatus,
    modifier: Modifier = Modifier
) {
    val (bg, fg, icon) = when (status) {
        DoseStatus.TAKEN -> Triple(StatusTakenContainer, StatusTaken, Icons.Default.CheckCircle)
        DoseStatus.DUE -> Triple(StatusDueContainer, StatusDue, Icons.Default.NotificationsActive)
        DoseStatus.UPCOMING -> Triple(StatusUpcomingContainer, StatusUpcoming, Icons.Default.Schedule)
        DoseStatus.SKIPPED -> Triple(StatusSkippedContainer, StatusSkipped, Icons.Default.Close)
        DoseStatus.MISSED -> Triple(StatusMissedContainer, StatusMissed, Icons.Default.Warning)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = status.displayName,
                tint = fg,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status.displayName,
                color = fg,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
