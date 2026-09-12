package com.example.ui.screens.parent

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
fun ParentConnectChildScreen(
    onContinueToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val pairingState by AppDataContainer.pairingService.pairingState.collectAsState()

    var enteredCode by remember { mutableStateOf("482731") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

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
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        if (pairingState.status == ConnectionStatus.CONNECTED) StatusTakenContainer else SapphireContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (pairingState.status == ConnectionStatus.CONNECTED) Icons.Default.Verified else Icons.Default.ChildCare,
                    contentDescription = null,
                    tint = if (pairingState.status == ConnectionStatus.CONNECTED) StatusTaken else SapphirePrimary,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // CASE 1: CHILD CONNECTED SUCCESSFULLY
            // ==========================================
            if (pairingState.status == ConnectionStatus.CONNECTED) {
                Text(
                    text = "Connected to Arjun!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "You are now connected as Arjun's guardian. You can monitor medicine schedules, adherence, and receive alerts.",
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
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ChildCare, contentDescription = null, tint = StatusTaken, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(text = "Arjun", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF065F46))
                            Text(text = "Age 12 • 3 Active Prescriptions", fontSize = 13.sp, color = Color(0xFF047857))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onContinueToHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("parent_continue_to_home_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SapphirePrimary)
                ) {
                    Text("Go to Parent Dashboard", fontWeight = FontWeight.Bold)
                }

            // ==========================================
            // CASE 2: WAITING FOR APPROVAL
            // ==========================================
            } else if (pairingState.status == ConnectionStatus.WAITING_FOR_CHILD_APPROVAL || pairingState.status == ConnectionStatus.PARENT_WANTS_TO_CONNECT) {
                Text(
                    text = "Waiting for Approval",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "We sent your connection request to Arjun's device. Waiting for the child to accept...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.alpha(pulseAlpha)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = SapphirePrimary,
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Waiting for child to approve...",
                        fontWeight = FontWeight.SemiBold,
                        color = SapphirePrimary,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Simulation Trigger to approve connection instantly
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🧪 Prototype Testing Action",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SapphirePrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Simulate that Arjun tapped 'Accept' on their screen:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    AppDataContainer.pairingService.simulateChildApproval()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SapphirePrimary),
                            modifier = Modifier.testTag("simulate_child_approval_button")
                        ) {
                            Text("Simulate Child Approval (Arjun)")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        scope.launch {
                            AppDataContainer.pairingService.cancelPairing()
                        }
                    }
                ) {
                    Text("Cancel Request", color = MaterialTheme.colorScheme.error)
                }

            // ==========================================
            // CASE 3: INPUT 6-DIGIT CODE
            // ==========================================
            } else {
                Text(
                    text = "Connect to Your Child",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ask your child for their 6-digit pairing code shown on their MediRemind screen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

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
                    Spacer(modifier = Modifier.height(16.dp))
                }

                OutlinedTextField(
                    value = enteredCode,
                    onValueChange = { input ->
                        val cleaned = input.filter { it.isDigit() || it.isWhitespace() }.take(7)
                        enteredCode = cleaned
                        errorMessage = null
                    },
                    label = { Text("6-Digit Pairing Code") },
                    placeholder = { Text("e.g., 482 731") },
                    leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("parent_pairing_code_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val digits = enteredCode.filter { it.isDigit() }
                        if (digits.length != 6) {
                            errorMessage = "Please enter a valid 6-digit code (e.g. 482 731)."
                            return@Button
                        }
                        isSubmitting = true
                        scope.launch {
                            delay(400)
                            isSubmitting = false
                            val success = AppDataContainer.pairingService.submitCodeFromParent(digits)
                            if (!success) {
                                errorMessage = "Invalid code. Please verify the code displayed on child's screen."
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_parent_code_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SapphirePrimary)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White)
                    } else {
                        Text("Connect Child", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(onClick = onContinueToHome) {
                    Text("Skip for now and enter dashboard", fontSize = 13.sp)
                }
            }
        }
    }
}
