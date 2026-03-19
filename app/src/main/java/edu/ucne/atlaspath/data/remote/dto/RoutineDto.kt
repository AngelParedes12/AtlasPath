package edu.ucne.atlaspath.data.remote.dto

import edu.ucne.atlaspath.data.local.entity.RoutineEntity
import edu.ucne.atlaspath.domain.model.Rutina
import java.util.UUID

// Este es el objeto principal que Gemini DEBE devolver
data class RoutineDto(
    val titulo: String? = "",
    val descripcion: String? = "",
    val ejercicios: List<EjercicioDto> = emptyList()
) {
    // Mapper a Dominio (para uso inmediato si fuera necesario, aunque SSOT prefiere pasar por Entity)
    fun toDomain() = Rutina(
        rutinaId = UUID.randomUUID().toString(), // Generamos ID local
        titulo = titulo ?: "Rutina sin título",
        descripcion = descripcion ?: "",
        ejercicios = ejercicios.map { it.toDomain() }
    )

    // Mapper crucial para Offline-First: DTO -> Entity (para guardar en Room)
    fun toEntity() = RoutineEntity(
        id = UUID.randomUUID().toString(),
        titulo = titulo ?: "Rutina generada por IA",
        descripcion = descripcion ?: "",
        ejercicios = ejercicios.map { it.toEntity() }
    )
}

data class EjercicioDto(
    val nombre: String? = "",
    val series: Int? = 0,
    val repeticiones: Int? = 0,
    val descansoSegundos: Int? = 0
) {
    fun toDomain() = edu.ucne.atlaspath.domain.model.Ejercicio(
        nombre = nombre ?: "",
        series = series ?: 0,
        repeticiones = repeticiones ?: 0,
        descansoSegundos = descansoSegundos ?: 0
    )

    // Para guardar dentro de la entidad de Room (requerirá TypeConverter)
    fun toEntity() = edu.ucne.atlaspath.data.local.entity.EjercicioEntity(
        nombre = nombre ?: "",
        series = series ?: 0,
        repeticiones = repeticiones ?: 0,
        descansoSegundos = descansoSegundos ?: 0
    )
}