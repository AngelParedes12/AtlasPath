package edu.ucne.atlaspath.presentation.onboarding

data class OnboardingUiState(
    val isLoading: Boolean = false,
    val nombre: String = "",
    val nivel: String = "Principiante",
    val objetivo: String = "Hipertrofia",
    val success: Boolean = false,
    val error: String? = null
)