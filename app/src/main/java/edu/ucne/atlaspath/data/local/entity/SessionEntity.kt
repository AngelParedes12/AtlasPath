package edu.ucne.atlaspath.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Sesiones")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val sesionId: Int? = null,
    val rutinaId: Int,
    val fechaInicio: Long,
    val fechaFin: Long,
    val volumenTotalLbs: Double,
    val xpGanada: Int,
    val registros: List<RegistroEntity>
)

data class RegistroEntity(
    val ejercicioNombre: String,
    val grupoMuscular: String,
    val pesoLbs: Double,
    val repeticionesHechas: Int
)