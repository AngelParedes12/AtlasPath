package edu.ucne.atlaspath.presentation.tareas.aicreator

import edu.ucne.atlaspath.domain.model.Rutina

data class AiCreatorUiState(
    val isLoading: Boolean = false,
    val prompt: String = "",
    val rutinaGenerada: Rutina? = null,
    val error: String? = null,
    val isSaved: Boolean = false
)