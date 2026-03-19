package edu.ucne.atlaspath.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.ucne.atlaspath.domain.model.Rutina

@Entity(tableName = "rutinas")
data class RoutineEntity(
    @PrimaryKey val id: String, // Usaremos UUID generado
    val titulo: String,
    val descripcion: String,
    val ejercicios: List<EjercicioEntity>, // Requiere TypeConverter
    val fechaCreacion: Long = System.currentTimeMillis()
) {
    fun toDomain() = Rutina(
        rutinaId = id,
        titulo = titulo,
        descripcion = descripcion,
        ejercicios = ejercicios.map { it.toDomain() }
    )
}

data class EjercicioEntity(
    val nombre: String,
    val series: Int,
    val repeticiones: Int,
    val descansoSegundos: Int
) {
    fun toDomain() = edu.ucne.atlaspath.domain.model.Ejercicio(
        nombre = nombre,
        series = series,
        repeticiones = repeticiones,
        descansoSegundos = descansoSegundos
    )
}