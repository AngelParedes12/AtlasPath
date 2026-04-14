package edu.ucne.atlaspath.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Exercises")
data class ExerciseEntity(
    @PrimaryKey val id: String,
    val name: String,
    val target: String,
    val bodyPart: String,
    val gifUrl: String = "",
    val equipment: String = "Cuerpo / Pesas"
)