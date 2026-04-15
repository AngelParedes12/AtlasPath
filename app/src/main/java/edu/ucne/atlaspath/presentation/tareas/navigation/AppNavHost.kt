@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package edu.ucne.atlaspath.presentation.tareas.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import edu.ucne.atlaspath.presentation.tareas.dashboard.DashboardScreen
import edu.ucne.atlaspath.presentation.tareas.dashboard.DashboardViewModel
import edu.ucne.atlaspath.presentation.tareas.onboarding.OnboardingScreen
import edu.ucne.atlaspath.presentation.tareas.onboarding.PhysicalProfileScreen
import edu.ucne.atlaspath.presentation.tareas.onboarding.SanctuaryLoadingScreen
import edu.ucne.atlaspath.presentation.tareas.rutina.detail.DetailRutinaScreen
import edu.ucne.atlaspath.presentation.tareas.rutina.list.ListRutinaScreen
import edu.ucne.atlaspath.presentation.tareas.aicreator.AiCreatorScreen
import edu.ucne.atlaspath.presentation.tareas.liveworkout.LiveWorkoutScreen
import edu.ucne.atlaspath.presentation.tareas.explore.ExerciseExplorerScreen
import edu.ucne.atlaspath.presentation.tareas.history.HistoryScreen
import edu.ucne.atlaspath.presentation.tareas.calendario.FullCalendarScreen
import edu.ucne.atlaspath.presentation.tareas.nutrition.NutritionScreen
import edu.ucne.atlaspath.presentation.tareas.profile.ProfileScreen
import edu.ucne.atlaspath.presentation.tareas.profile.EditProfileScreen

val LocalSnackbarHost = compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

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
                route.contains("Nutrition") ||
                route.contains("Profile")
    } ?: false

    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(LocalSnackbarHost provides snackbarHostState) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.95f),
                        contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            },
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
                    OnboardingScreen(onFinish = { navController.navigate(Screen.PhysicalProfile) })
                }

                composable<Screen.PhysicalProfile> {
                    PhysicalProfileScreen(
                        onBack = { navController.popBackStack() },
                        onProfileSaved = {
                            navController.navigate(Screen.SanctuaryLoading) {
                                popUpTo<Screen.Onboarding> { inclusive = true }
                            }
                        }
                    )
                }

                composable<Screen.SanctuaryLoading> {
                    SanctuaryLoadingScreen(
                        onLoadingComplete = {
                            navController.navigate(Screen.Dashboard) {
                                popUpTo<Screen.SanctuaryLoading> { inclusive = true }
                            }
                        }
                    )
                }

                composable<Screen.Dashboard> {
                    val dashboardViewModel: DashboardViewModel = hiltViewModel()
                    val dashboardState by dashboardViewModel.state.collectAsStateWithLifecycle()

                    DashboardScreen(
                        viewModel = dashboardViewModel,
                        onNavigateToBiblioteca = { navController.navigate(Screen.RutinaList) },
                        onNavigateToRutina = { id -> navController.navigate(Screen.LiveWorkout(id)) },
                        onCreateRutina = { navController.navigate(Screen.RutinaDetail(0)) },
                        onNavigateToHistorial = { navController.navigate(Screen.History) },
                        onNavigateToCalendar = { navController.navigate(Screen.FullCalendar) },
                        onNavigateToNutrition = { navController.navigate(Screen.Nutrition(dashboardState.pesoActualLbs.toFloat())) }
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
                        onNavigateToAi = {
                            navController.navigate(Screen.AiCreator) {
                                popUpTo<Screen.RutinaDetail> { inclusive = true }
                            }
                        }
                    )
                }

                composable<Screen.AiCreator> {
                    AiCreatorScreen(
                        onBack = { navController.navigateUp() },
                        onNavigateToLiveWorkout = { id ->
                            navController.navigate(Screen.LiveWorkout(id)) {
                                popUpTo<Screen.AiCreator> { inclusive = true }
                            }
                        }
                    )
                }

                composable<Screen.Nutrition> {
                    NutritionScreen(onBack = { navController.navigateUp() })
                }

                composable<Screen.Profile> {
                    ProfileScreen(
                        onNavigateToEditProfile = { navController.navigate(Screen.EditProfile) },
                        onLogout = {
                            navController.navigate(Screen.Onboarding) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable<Screen.EditProfile> {
                    EditProfileScreen(onBack = { navController.navigateUp() })
                }

                composable<Screen.LiveWorkout> {
                    LiveWorkoutScreen(
                        onFinish = {
                            navController.navigate(Screen.Dashboard) {
                                popUpTo<Screen.Dashboard> { inclusive = true }
                            }
                        },
                        onCancel = { navController.navigateUp() }
                    )
                }

                composable<Screen.History> { HistoryScreen(onBack = { navController.navigateUp() }) }

                composable<Screen.ExerciseExplorer> { ExerciseExplorerScreen(onBack = { navController.navigateUp() }) }

                composable<Screen.FullCalendar> { FullCalendarScreen(onBack = { navController.navigateUp() }) }
            }
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
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            label = { Text("Inicio", fontWeight = if (isDashboard) FontWeight.Bold else FontWeight.Normal) },
            selected = isDashboard,
            colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer, selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer),
            onClick = { if (!isDashboard) { navController.navigate(Screen.Dashboard) { popUpTo(navController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } } }
        )

        val isRutinaList = currentRoute?.contains("RutinaList") == true
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
            label = { Text("Biblioteca", fontWeight = if (isRutinaList) FontWeight.Bold else FontWeight.Normal) },
            selected = isRutinaList,
            colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.secondaryContainer, selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer),
            onClick = { if (!isRutinaList) { navController.navigate(Screen.RutinaList) { popUpTo(navController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } } }
        )

        val isNutrition = currentRoute?.contains("Nutrition") == true
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Restaurant, contentDescription = null) },
            label = { Text("Nutrición", fontWeight = if (isNutrition) FontWeight.Bold else FontWeight.Normal) },
            selected = isNutrition,
            colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.tertiaryContainer, selectedIconColor = MaterialTheme.colorScheme.onTertiaryContainer),
            onClick = { if (!isNutrition) { navController.navigate(Screen.Nutrition(0f)) { popUpTo(navController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } } }
        )

        val isProfile = currentRoute?.contains("Profile") == true
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            label = { Text("Perfil", fontWeight = if (isProfile) FontWeight.Bold else FontWeight.Normal) },
            selected = isProfile,
            colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer, selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer),
            onClick = { if (!isProfile) { navController.navigate(Screen.Profile) { popUpTo(navController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } } }
        )
    }
}