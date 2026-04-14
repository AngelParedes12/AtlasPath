package edu.ucne.atlaspath.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecipeDto(
    val titulo: String,
    val tiempoPreparacion: String,
    val ingredientes: List<String>,
    val instrucciones: List<String>,
    val caloriasEstimadas: Int,
    val proteinaEstimada: Float
)