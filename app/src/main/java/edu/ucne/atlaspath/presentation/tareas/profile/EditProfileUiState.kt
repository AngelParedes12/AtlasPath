package edu.ucne.atlaspath.presentation.tareas.profile

data class EditProfileUiState(
    val nombre: String = "Angel Paredes",
    val pesoLbs: Float = 165f,
    val alturaCm: Float = 175f,
    val nivel: String = "Intermedio",
    val objetivo: String = "Hipertrofia",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)