package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.model.UserRole
import com.example.data.repository.AppDataContainer
import com.example.ui.components.AppHeader
import com.example.ui.components.DemoControlPanel
import com.example.ui.screens.auth.EmailVerificationScreen
import com.example.ui.screens.auth.ForgotPasswordScreen
import com.example.ui.screens.auth.SignInScreen
import com.example.ui.screens.auth.SignUpScreen
import com.example.ui.screens.child.AddEditMedicineScreen
import com.example.ui.screens.child.ChildConnectParentScreen
import com.example.ui.screens.child.ChildHomeScreen
import com.example.ui.screens.child.ChildMedicinesScreen
import com.example.ui.screens.child.ChildRemindersScreen
import com.example.ui.screens.child.MedicineDetailScreen
import com.example.ui.screens.common.HistoryScreen
import com.example.ui.screens.parent.ParentConnectChildScreen
import com.example.ui.screens.parent.ParentHomeScreen
import com.example.ui.screens.role_selection.RoleSelectionScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MediRemindApp()
            }
        }
    }
}

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    // Child Items
    object ChildHome : BottomNavItem("child_home", "Home", Icons.Default.Home)
    object ChildMedicines : BottomNavItem("child_medicines", "Medicines", Icons.Default.Medication)
    object ChildReminders : BottomNavItem("child_reminders", "Reminders", Icons.Default.Notifications)
    object ChildHistory : BottomNavItem("child_history", "History", Icons.Default.History)
    object ChildSettings : BottomNavItem("child_settings", "Settings", Icons.Default.Settings)

    // Parent Items
    object ParentHome : BottomNavItem("parent_home", "Home", Icons.Default.Home)
    object ParentMedicines : BottomNavItem("parent_medicines", "Medicines", Icons.Default.Medication)
    object ParentHistory : BottomNavItem("parent_history", "History", Icons.Default.History)
    object ParentConnect : BottomNavItem("parent_connect", "Connect", Icons.Default.PersonAdd)
    object ParentSettings : BottomNavItem("parent_settings", "Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediRemindApp() {
    val navController = rememberNavController()
    val authSession by AppDataContainer.authService.sessionState.collectAsState()
    val isSyncing by AppDataContainer.syncService.isSyncing.collectAsState()

    var showDemoPanel by remember { mutableStateOf(false) }
    var selectedRoleForAuth by remember { mutableStateOf(UserRole.CHILD) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val childBottomItems = listOf(
        BottomNavItem.ChildHome,
        BottomNavItem.ChildMedicines,
        BottomNavItem.ChildReminders,
        BottomNavItem.ChildHistory,
        BottomNavItem.ChildSettings
    )

    val parentBottomItems = listOf(
        BottomNavItem.ParentHome,
        BottomNavItem.ParentMedicines,
        BottomNavItem.ParentHistory,
        BottomNavItem.ParentConnect,
        BottomNavItem.ParentSettings
    )

    val isChildMainRoute = childBottomItems.any { it.route == currentRoute }
    val isParentMainRoute = parentBottomItems.any { it.route == currentRoute }
    val showAppChrome = isChildMainRoute || isParentMainRoute

    Scaffold(
        topBar = {
            if (showAppChrome) {
                AppHeader(
                    role = authSession.role,
                    isSyncing = isSyncing,
                    onOpenDemoTools = { showDemoPanel = true }
                )
            }
        },
        bottomBar = {
            if (showAppChrome) {
                val currentItems = if (authSession.role == UserRole.CHILD) childBottomItems else parentBottomItems
                NavigationBar(
                    tonalElevation = 3.dp,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    currentItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            modifier = Modifier.testTag("nav_${item.route}")
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "role_selection",
            modifier = Modifier.padding(innerPadding)
        ) {
            // ----------------------------------------------------
            // 1. First Screen: Role Selection
            // ----------------------------------------------------
            composable("role_selection") {
                RoleSelectionScreen(
                    onSelectRole = { role ->
                        selectedRoleForAuth = role
                        navController.navigate("sign_in")
                    },
                    onDirectDemoEnter = { role ->
                        AppDataContainer.switchRoleForTesting(role)
                        if (role == UserRole.CHILD) {
                            navController.navigate("child_home") {
                                popUpTo("role_selection") { inclusive = true }
                            }
                        } else {
                            navController.navigate("parent_home") {
                                popUpTo("role_selection") { inclusive = true }
                            }
                        }
                    }
                )
            }

            // ----------------------------------------------------
            // 2. Auth Flow (Sign In, Sign Up, Verification, Forgot Password)
            // ----------------------------------------------------
            composable("sign_in") {
                SignInScreen(
                    role = selectedRoleForAuth,
                    onSignInSuccess = {
                        if (selectedRoleForAuth == UserRole.CHILD) {
                            navController.navigate("child_home") {
                                popUpTo("role_selection") { inclusive = true }
                            }
                        } else {
                            navController.navigate("parent_home") {
                                popUpTo("role_selection") { inclusive = true }
                            }
                        }
                    },
                    onNavigateToSignUp = { navController.navigate("sign_up") },
                    onNavigateToForgotPassword = { navController.navigate("forgot_password") },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("sign_up") {
                SignUpScreen(
                    role = selectedRoleForAuth,
                    onSignUpSuccess = {
                        navController.navigate("email_verification")
                    },
                    onNavigateToSignIn = { navController.navigate("sign_in") },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("email_verification") {
                EmailVerificationScreen(
                    userEmail = if (selectedRoleForAuth == UserRole.CHILD) "arjun.sharma@example.com" else "rahul.sharma@example.com",
                    onVerificationSuccess = {
                        if (selectedRoleForAuth == UserRole.CHILD) {
                            navController.navigate("child_connect_parent") {
                                popUpTo("role_selection") { inclusive = true }
                            }
                        } else {
                            navController.navigate("parent_connect_child") {
                                popUpTo("role_selection") { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable("forgot_password") {
                ForgotPasswordScreen(onBack = { navController.popBackStack() })
            }

            // ----------------------------------------------------
            // 3. Child Flow Screens
            // ----------------------------------------------------
            composable("child_connect_parent") {
                ChildConnectParentScreen(
                    onContinueToHome = {
                        navController.navigate("child_home") {
                            popUpTo("child_connect_parent") { inclusive = true }
                        }
                    }
                )
            }

            composable("child_home") {
                ChildHomeScreen(
                    onNavigateToAddMedicine = { navController.navigate("add_medicine") },
                    onNavigateToMedicineDetails = { medId -> navController.navigate("medicine_detail/$medId") },
                    onNavigateToMedicinesList = { navController.navigate("child_medicines") },
                    onNavigateToConnectedFamily = { navController.navigate("child_connect_parent") }
                )
            }

            composable("child_medicines") {
                ChildMedicinesScreen(
                    onNavigateToAddMedicine = { navController.navigate("add_medicine") },
                    onNavigateToEditMedicine = { medId -> navController.navigate("edit_medicine/$medId") },
                    onNavigateToDetails = { medId -> navController.navigate("medicine_detail/$medId") },
                    isParentView = false
                )
            }

            composable("child_reminders") {
                ChildRemindersScreen(
                    onNavigateToMedicineDetails = { medId -> navController.navigate("medicine_detail/$medId") },
                    isParentView = false
                )
            }

            composable("child_history") {
                HistoryScreen(isParentView = false)
            }

            composable("child_settings") {
                SettingsScreen(
                    onNavigateToPairing = { navController.navigate("child_connect_parent") },
                    onSignOut = {
                        navController.navigate("role_selection") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onOpenDemoTools = { showDemoPanel = true }
                )
            }

            // ----------------------------------------------------
            // 4. Parent Flow Screens
            // ----------------------------------------------------
            composable("parent_connect_child") {
                ParentConnectChildScreen(
                    onContinueToHome = {
                        navController.navigate("parent_home") {
                            popUpTo("parent_connect_child") { inclusive = true }
                        }
                    }
                )
            }

            composable("parent_connect") {
                ParentConnectChildScreen(
                    onContinueToHome = {
                        navController.navigate("parent_home")
                    }
                )
            }

            composable("parent_home") {
                ParentHomeScreen(
                    onNavigateToMedicines = { navController.navigate("parent_medicines") },
                    onNavigateToHistory = { navController.navigate("parent_history") },
                    onNavigateToMedicineDetails = { medId -> navController.navigate("medicine_detail/$medId") },
                    onNavigateToPairing = { navController.navigate("parent_connect") }
                )
            }

            composable("parent_medicines") {
                ChildMedicinesScreen(
                    onNavigateToAddMedicine = { },
                    onNavigateToEditMedicine = { },
                    onNavigateToDetails = { medId -> navController.navigate("medicine_detail/$medId") },
                    isParentView = true
                )
            }

            composable("parent_history") {
                HistoryScreen(isParentView = true)
            }

            composable("parent_settings") {
                SettingsScreen(
                    onNavigateToPairing = { navController.navigate("parent_connect") },
                    onSignOut = {
                        navController.navigate("role_selection") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onOpenDemoTools = { showDemoPanel = true }
                )
            }

            // ----------------------------------------------------
            // 5. Shared Medicine Detail & Add/Edit
            // ----------------------------------------------------
            composable("add_medicine") {
                AddEditMedicineScreen(
                    medicineId = null,
                    onSaveSuccess = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "edit_medicine/{medicineId}",
                arguments = listOf(navArgument("medicineId") { type = NavType.StringType })
            ) { backStackEntry ->
                val medicineId = backStackEntry.arguments?.getString("medicineId")
                AddEditMedicineScreen(
                    medicineId = medicineId,
                    onSaveSuccess = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "medicine_detail/{medicineId}",
                arguments = listOf(navArgument("medicineId") { type = NavType.StringType })
            ) { backStackEntry ->
                val medicineId = backStackEntry.arguments?.getString("medicineId") ?: ""
                MedicineDetailScreen(
                    medicineId = medicineId,
                    onNavigateToEdit = { medId -> navController.navigate("edit_medicine/$medId") },
                    onBack = { navController.popBackStack() },
                    isParentView = (authSession.role == UserRole.PARENT)
                )
            }
        }
    }

    // Interactive Demo / Testing Control Panel
    if (showDemoPanel) {
        DemoControlPanel(
            currentRole = authSession.role,
            onRoleChange = { newRole ->
                if (newRole == UserRole.CHILD) {
                    navController.navigate("child_home") {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                } else {
                    navController.navigate("parent_home") {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }
            },
            onDismiss = { showDemoPanel = false }
        )
    }
}
