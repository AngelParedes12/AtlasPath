package edu.ucne.atlaspath.presentation.tareas.explore

import edu.ucne.atlaspath.data.remote.dto.ExerciseDto

data class ExplorerUiState(
    val isLoading: Boolean = false,
    val exercises: List<ExerciseDto> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
)