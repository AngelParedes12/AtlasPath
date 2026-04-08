package edu.ucne.atlaspath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import edu.ucne.atlaspath.data.local.datastore.UserPreferences
import edu.ucne.atlaspath.presentation.tareas.navigation.AppNavHost
import edu.ucne.atlaspath.presentation.tareas.navigation.Screen
import edu.ucne.atlaspath.ui.theme.AtlasPathTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AtlasPathTheme {
                val isOnboardingCompleted by userPreferences.isOnboardingCompleted.collectAsStateWithLifecycle(initialValue = null)

                if (isOnboardingCompleted == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    val navController = rememberNavController()
                    val startDestination = if (isOnboardingCompleted == true) {
                        Screen.Dashboard
                    } else {
                        Screen.Onboarding
                    }
                    AppNavHost(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}