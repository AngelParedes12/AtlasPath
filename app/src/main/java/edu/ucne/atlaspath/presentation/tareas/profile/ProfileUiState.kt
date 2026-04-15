package edu.ucne.atlaspath.presentation.tareas.profile
data class ProfileUiState(
    val userName: String = "",
    val nivelActual: Int = 1,
    val rangoNombre: String = "Principiante",
    val isLoading: Boolean = false
)