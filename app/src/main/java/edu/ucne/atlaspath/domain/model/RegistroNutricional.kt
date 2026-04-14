package edu.ucne.atlaspath.domain.model

data class RegistroNutricional(
    val id: Int = 0,
    val comidaTexto: String,
    val calorias: Int,
    val proteina: Float,
    val carbohidratos: Float,
    val grasa: Float,
    val fechaFormateada: String,
    val timestamp: Long
)