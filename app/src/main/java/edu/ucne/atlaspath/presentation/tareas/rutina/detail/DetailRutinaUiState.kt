package edu.ucne.atlaspath.presentation.tareas.rutina.detail

import edu.ucne.atlaspath.data.remote.dto.ExerciseDto
import edu.ucne.atlaspath.domain.model.Ejercicio

data class DetailRutinaUiState(
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val rutinaId: Int = 0,
    val titulo: String = "",
    val tituloError: String? = null,
    val descripcion: String = "",
    val ejercicios: List<Ejercicio> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<ExerciseDto> = emptyList()
)