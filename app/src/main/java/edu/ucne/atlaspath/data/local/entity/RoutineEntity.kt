package edu.ucne.atlaspath.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Rutinas")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true)
    val rutinaId: Int? = null,
    val titulo: String,
    val descripcion: String,
    val ejercicios: List<EjercicioEntity>
)

data class EjercicioEntity(
    val nombre: String,
    val series: Int,
    val repeticiones: Int,
    val descansoSegundos: Int,
    val grupoMuscular: String? = "General"
)