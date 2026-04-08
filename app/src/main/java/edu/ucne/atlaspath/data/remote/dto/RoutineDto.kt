package edu.ucne.atlaspath.data.remote.dto

import edu.ucne.atlaspath.domain.model.Ejercicio
import edu.ucne.atlaspath.domain.model.Rutina

data class RoutineDto(
    val titulo: String? = "",
    val descripcion: String? = "",
    val ejercicios: List<EjercicioDto> = emptyList()
)

data class EjercicioDto(
    val nombre: String? = "",
    val series: Int? = 0,
    val repeticiones: Int? = 0,
    val descansoSegundos: Int? = 0,
    val grupoMuscular: String? = "General"
)

fun RoutineDto.toDomain() = Rutina(
    rutinaId = 0,
    titulo = titulo ?: "Rutina generada por IA",
    descripcion = descripcion ?: "",
    ejercicios = ejercicios.map { it.toDomain() }
)

fun EjercicioDto.toDomain() = Ejercicio(
    nombre = nombre ?: "",
    series = series ?: 0,
    repeticiones = repeticiones ?: 0,
    descansoSegundos = descansoSegundos ?: 0,
    grupoMuscular = grupoMuscular ?: "General"
)