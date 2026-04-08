package edu.ucne.atlaspath.presentation.dashboard

import edu.ucne.atlaspath.domain.model.Rutina
import edu.ucne.atlaspath.domain.useCase.RangoMuscular

data class DashboardUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userName: String = "",
    val pesoActualLbs: Double = 0.0,
    val rutinaHoy: Rutina? = null,
    val diasEntrenadosSemana: List<Boolean> = List(7) { false },
    val rachaDias: Int = 0,
    val totalEntrenamientos: Int = 0,
    val volumenTotalLbs: Double = 0.0,
    val nivelActual: Int = 1,
    val progresoNivel: Float = 0f,
    val rangosMusculares: List<RangoMuscular> = emptyList(),
)