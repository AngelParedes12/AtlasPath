package edu.ucne.atlaspath.domain.model

data class sesion(
    val sesionId: Int = 0,
    val rutinaId: Int,
    val fechaInicio: Long,
    val fechaFin: Long,
    val volumenTotalLbs: Double = 0.0,
    val xpGanada: Int = 0,
    val registros: List<RegistroEjercicio> = emptyList(),
)

data class RegistroEjercicio(
    val ejercicioNombre: String,
    val grupoMuscular: String,
    val pesoLbs: Double,
    val repeticionesHechas: Int,
)