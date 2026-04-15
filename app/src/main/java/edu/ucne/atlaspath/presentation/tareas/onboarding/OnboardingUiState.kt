package edu.ucne.atlaspath.presentation.tareas.onboarding

data class OnboardingUiState(
    val nombre: String = "",
    val nivel: String = "Principiante",
    val objetivo: String = "Hipertrofia",
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)