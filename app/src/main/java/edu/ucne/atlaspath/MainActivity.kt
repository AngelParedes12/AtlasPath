package edu.ucne.atlaspath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import edu.ucne.atlaspath.data.local.datastore.UserPreferences
import edu.ucne.atlaspath.presentation.navigation.AppNavHost
import edu.ucne.atlaspath.presentation.navigation.Screen
import edu.ucne.atlaspath.ui.theme.AtlasPathTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Inyectamos tus preferencias para saber si es la primera vez que abre la app
    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AtlasPathTheme {
                // Leemos el estado del Onboarding (initialValue null para saber que está cargando)
                val isOnboardingCompleted by userPreferences.isOnboardingCompleted.collectAsStateWithLifecycle(initialValue = null)

                if (isOnboardingCompleted == null) {
                    // Muestra una pequeña rueda de carga los primeros milisegundos
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val navController = rememberNavController()

                    // Lógica de enrutamiento inicial
                    val startDestination = if (isOnboardingCompleted == true) {
                        Screen.Dashboard
                    } else {
                        Screen.Onboarding
                    }

                    // Llamamos a nuestro Gestor de Navegación Maestro
                    AppNavHost(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}