package edu.ucne.atlaspath.domain.model

data class Dashboard(
    val totalEntrenamientos: Int = 0,
    val volumenTotalLbs: Double = 0.0,
    val rachaDias: Int = 0,
    val nivelActual: Int = 1,
    val progresoNivel: Float = 0f,
    val rangosMusculares: List<RangoMuscular> = emptyList()
)

data class RangoMuscular(
    val musculo: String,
    val rangoNombre: String,
    val progreso: Float,
    val medalla: String,
    val pesoMaximoLbs: Double
)