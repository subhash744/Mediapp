package com.example.ui.screens.child

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectionStatus
import com.example.data.model.FamilyMember
import com.example.data.model.UserRole
import com.example.data.repository.AppDataContainer
import com.example.ui.theme.SapphireContainer
import com.example.ui.theme.SapphirePrimary
import com.example.ui.theme.StatusTaken
import com.example.ui.theme.StatusTakenContainer
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChildConnectParentScreen(
    onContinueToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pairingState by AppDataContainer.pairingService.pairingState.collectAsState()

    var remainingSeconds by remember { mutableLongStateOf(582L) }
    var showAcceptConfirmDialog by remember { mutableStateOf(false) }

    // Countdown timer tick
    LaunchedEffect(Unit) {
        while (remainingSeconds > 0) {
            delay(1000)
            remainingSeconds--
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val countdownText = String.format("%02d:%02d", minutes, seconds)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Screen Header
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        when (pairingState.status) {
                            ConnectionStatus.CONNECTED -> StatusTakenContainer
                            ConnectionStatus.PARENT_WANTS_TO_CONNECT -> SapphireContainer
                            else -> TealContainer
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (pairingState.status) {
                        ConnectionStatus.CONNECTED -> Icons.Default.Verified
                        ConnectionStatus.PARENT_WANTS_TO_CONNECT -> Icons.Default.SupervisorAccount
                        else -> Icons.Default.SupervisorAccount
                    },
                    contentDescription = null,
                    tint = when (pairingState.status) {
                        ConnectionStatus.CONNECTED -> StatusTaken
                        ConnectionStatus.PARENT_WANTS_TO_CONNECT -> SapphirePrimary
                        else -> TealPrimary
                    },
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // CASE 1: PARENT CONNECTED SUCCESSFULLY
            // ==========================================
            if (pairingState.status == ConnectionStatus.CONNECTED) {
                Text(
                    text = "Parent Connected!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Rahul (ra***@gmail.com) is now linked as your guardian.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = StatusTakenContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusTaken, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Rahul", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF065F46))
                            Text(text = "ra***@gmail.com • Guardian", fontSize = 13.sp, color = Color(0xFF047857))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onContinueToHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("child_continue_to_home_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Go to My Medicine Dashboard", fontWeight = FontWeight.Bold)
                }

            // ==========================================
            // CASE 2: PARENT WANTS TO CONNECT (IN-PLACE)
            // ==========================================
            } else if (pairingState.status == ConnectionStatus.PARENT_WANTS_TO_CONNECT) {
                Text(
                    text = "Parent wants to connect",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "A parent is requesting to monitor your medicine adherence and reminders.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Limited Parent Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(SapphireContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = SapphirePrimary, modifier = Modifier.size(32.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = pairingState.pendingParent?.name ?: "Rahul",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = pairingState.pendingParent?.maskedEmail ?: "ra***@gmail.com",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Permissions requested:\n• View scheduled medicines\n• Check dose completion & adherence",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Accept & Reject Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                AppDataContainer.pairingService.rejectParentConnection()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("reject_parent_connection_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reject")
                    }

                    Button(
                        onClick = { showAcceptConfirmDialog = true },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(50.dp)
                            .testTag("accept_parent_connection_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Accept", fontWeight = FontWeight.Bold)
                    }
                }

            // ==========================================
            // CASE 3: DEFAULT WAITING FOR PARENT
            // ==========================================
            } else {
                Text(
                    text = "Connect Your Parent",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Share this 6-digit temporary pairing code with your parent to link your accounts.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // 6-digit Pairing Code Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(2.dp, TealPrimary.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = pairingState.code,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 6.sp,
                            color = TealPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HourglassEmpty,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Code expires in $countdownText",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Copy Code & Share Code Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Pairing Code", pairingState.code))
                            Toast.makeText(context, "Pairing code copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("copy_code_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Code")
                    }

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "MediRemind Parent Pairing Code")
                                putExtra(Intent.EXTRA_TEXT, "Here is my MediRemind pairing code to connect as parent: ${pairingState.code}")
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Pairing Code"))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("share_code_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Code")
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Pulsing "Waiting for your parent..."
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.alpha(pulseAlpha)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(TealPrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Waiting for your parent...",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TealPrimary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Simulation Trigger Button for prototype testing
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🧪 Prototype Testing Action",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Click below to simulate an incoming connection request from parent Rahul:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    AppDataContainer.pairingService.simulateIncomingParentRequest(
                                        FamilyMember(
                                            id = "parent-rahul-01",
                                            name = "Rahul",
                                            maskedEmail = "ra***@gmail.com",
                                            fullEmail = "rahul.sharma@example.com",
                                            role = UserRole.PARENT
                                        )
                                    )
                                }
                            },
                            modifier = Modifier.testTag("simulate_parent_request_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Simulate Parent Connection Request", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onContinueToHome) {
                    Text("Skip for now & setup medicines first", fontSize = 13.sp)
                }
            }
        }
    }

    // Require Confirmation Before Accepting
    if (showAcceptConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showAcceptConfirmDialog = false },
            title = {
                Text(text = "Connect with Rahul?")
            },
            text = {
                Text(
                    text = "Are you sure Rahul (ra***@gmail.com) is your parent or guardian? They will be able to see your medicines and scheduled doses.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAcceptConfirmDialog = false
                        scope.launch {
                            AppDataContainer.pairingService.acceptParentConnection()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    modifier = Modifier.testTag("confirm_accept_parent_button")
                ) {
                    Text("Yes, Connect Parent")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAcceptConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
