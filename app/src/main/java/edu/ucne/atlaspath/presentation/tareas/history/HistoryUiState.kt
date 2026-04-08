package edu.ucne.atlaspath.presentation.tareas.history

import edu.ucne.atlaspath.domain.model.Sesion


data class HistoryUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val sesiones: List<Sesion> = emptyList(),
    val xpAcumulada: Int = 0,
    val volumenTotalHistorico: Double = 0.0
)