package edu.ucne.atlaspath.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import edu.ucne.atlaspath.presentation.dashboard.DashboardScreen
import edu.ucne.atlaspath.presentation.onboarding.OnboardingScreen
import edu.ucne.atlaspath.presentation.onboarding.PhysicalProfileScreen
import edu.ucne.atlaspath.presentation.tareas.detail.DetailRutinaScreen
import edu.ucne.atlaspath.presentation.tareas.list.ListRutinaScreen
import edu.ucne.atlaspath.presentation.aicreator.AiCreatorScreen
import edu.ucne.atlaspath.presentation.liveworkout.LiveWorkoutScreen
import edu.ucne.atlaspath.presentation.explorer.ExerciseExplorerScreen
import edu.ucne.atlaspath.presentation.history.HistoryScreen
import edu.ucne.atlaspath.presentation.onboarding.PhysicalProfileViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Any
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute?.let { route ->
        route.contains("Dashboard") ||
                route.contains("RutinaList") ||
                route.contains("AiCreator")
    } ?: false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Onboarding> {
                val viewModel: PhysicalProfileViewModel = hiltViewModel()
                OnboardingScreen(onFinish = { navController.navigate(Screen.PhysicalProfile) })
            }

            composable<Screen.PhysicalProfile> {
                val viewModel: PhysicalProfileViewModel = hiltViewModel()
                PhysicalProfileScreen(
                    onBack = { navController.popBackStack() },
                    onProfileSaved = { age, weight, height, somatotype, goal, level, gender ->
                        viewModel.saveProfile(age, weight, height, somatotype, goal, level, gender) {
                            navController.navigate(Screen.Dashboard) { popUpTo(Screen.Onboarding) { inclusive = true } }
                        }
                    }
                )
            }

            composable<Screen.Dashboard> {
                DashboardScreen(
                    onNavigateToGenerador = { navController.navigate(Screen.AiCreator) },
                    onNavigateToBiblioteca = { navController.navigate(Screen.RutinaList) },
                    onNavigateToRutina = { id -> navController.navigate(Screen.LiveWorkout(id)) },
                    onCreateRutina = { navController.navigate(Screen.RutinaDetail(0)) },
                    onNavigateToHistorial = { navController.navigate(Screen.History) },
                    onNavigateToCalendar = { navController.navigate(Screen.FullCalendar) }
                )
            }

            composable<Screen.RutinaList> {
                ListRutinaScreen(
                    onRutinaClick = { id -> navController.navigate(Screen.RutinaDetail(id)) },
                    onCreateRutina = { navController.navigate(Screen.RutinaDetail(0)) },
                    onTrainClick = { id -> navController.navigate(Screen.LiveWorkout(id)) }
                )
            }

            composable<Screen.RutinaDetail> {
                DetailRutinaScreen(
                    onBack = { navController.navigateUp() },
                    onNavigateToAi = { navController.navigate(Screen.AiCreator) { popUpTo(Screen.RutinaDetail) { inclusive = true } } }
                )
            }

            composable<Screen.AiCreator> {
                AiCreatorScreen(
                    onBack = { navController.navigateUp() },
                    onNavigateToLiveWorkout = { id -> navController.navigate(Screen.LiveWorkout(id)) { popUpTo(Screen.AiCreator) { inclusive = true } } }
                )
            }

            composable<Screen.LiveWorkout> {
                LiveWorkoutScreen(
                    onFinish = { navController.navigate(Screen.Dashboard) { popUpTo(Screen.Dashboard) { inclusive = true } } },
                    onCancel = { navController.navigateUp() }
                )
            }

            composable<Screen.History> { HistoryScreen(onBack = { navController.navigateUp() }) }
            composable<Screen.ExerciseExplorer> { ExerciseExplorerScreen(onBack = { navController.navigateUp() }) }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, currentRoute: String?) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).clip(RoundedCornerShape(24.dp))
    ) {
        val isDashboard = currentRoute?.contains("Dashboard") == true
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = { Text("Inicio", fontWeight = if (isDashboard) FontWeight.Bold else FontWeight.Normal) },
            selected = isDashboard,
            colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer, selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer),
            onClick = { if (!isDashboard) { navController.navigate(Screen.Dashboard) { popUpTo(navController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } } }
        )

        val isAiCreator = currentRoute?.contains("AiCreator") == true
        NavigationBarItem(
            icon = { Icon(Icons.Filled.AutoAwesome, contentDescription = "IA Creador") },
            label = { Text("IA Creador", fontWeight = if (isAiCreator) FontWeight.Bold else FontWeight.Normal) },
            selected = isAiCreator,
            colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.tertiaryContainer, selectedIconColor = MaterialTheme.colorScheme.onTertiaryContainer),
            onClick = { if (!isAiCreator) { navController.navigate(Screen.AiCreator) { popUpTo(navController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } } }
        )

        val isRutinaList = currentRoute?.contains("RutinaList") == true
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Biblioteca") },
            label = { Text("Biblioteca", fontWeight = if (isRutinaList) FontWeight.Bold else FontWeight.Normal) },
            selected = isRutinaList,
            colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer, selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer),
            onClick = { if (!isRutinaList) { navController.navigate(Screen.RutinaList) { popUpTo(navController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } } }
        )
    }
}