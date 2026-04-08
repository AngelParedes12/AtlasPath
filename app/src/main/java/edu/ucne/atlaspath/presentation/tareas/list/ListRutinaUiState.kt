package edu.ucne.atlaspath.presentation.tareas.list

import edu.ucne.atlaspath.domain.model.Rutina

data class ListRutinaUiState(
    val isLoading: Boolean = true,
    val rutinas: List<Rutina> = emptyList(),
    val error: String? = null
)