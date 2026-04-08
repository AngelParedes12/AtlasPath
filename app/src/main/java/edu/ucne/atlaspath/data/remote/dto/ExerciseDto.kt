package edu.ucne.atlaspath.data.remote.dto

import com.squareup.moshi.Json

data class ExerciseDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "bodyPart") val bodyPart: String?,
    @Json(name = "target") val target: String?,
    @Json(name = "equipment") val equipment: String?,
    @Json(name = "gifUrl") val gifUrl: String?,
    @Json(name = "instructions") val instructions: List<String>?
)