package edu.ucne.atlaspath.presentation.history

import edu.ucne.atlaspath.domain.model.sesion


data class HistoryUiState(
    val isLoading: Boolean = true,
    val sesiones: List<sesion> = emptyList(),
    val volumenTotalHistorico: Double = 0.0,
    val xpAcumulada: Int = 0,
    val error: String? = null
)