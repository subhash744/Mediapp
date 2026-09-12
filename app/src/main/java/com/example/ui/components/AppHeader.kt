package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.SapphireContainer
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary

@Composable
fun AppHeader(
    role: UserRole,
    isSyncing: Boolean,
    onOpenDemoTools: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand + Role
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (role == UserRole.CHILD) TealContainer else SapphireContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (role == UserRole.CHILD) Icons.Default.ChildCare else Icons.Default.SupervisorAccount,
                        contentDescription = "Role Icon",
                        tint = if (role == UserRole.CHILD) TealPrimary else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "MediRemind",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isSyncing) Color(0xFFF59E0B) else Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSyncing) "Syncing..." else "Realtime Synced",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Role indicator pill & Dev Demo tool button
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (role == UserRole.CHILD) TealContainer.copy(alpha = 0.7f) else SapphireContainer.copy(alpha = 0.7f)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (role == UserRole.CHILD) "Child Account" else "Parent Account",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (role == UserRole.CHILD) TealPrimary else MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Test Mode Tool Button
                IconButton(
                    onClick = onOpenDemoTools,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("demo_tools_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Open Test / Demo Panel",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
