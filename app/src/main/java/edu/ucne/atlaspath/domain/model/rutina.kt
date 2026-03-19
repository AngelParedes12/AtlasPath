package edu.ucne.atlaspath.domain.model

// Usando snake_case/minúsculas como en tu ejemplo de 'jugador'
data class rutina(
    val rutinaId: String,
    val titulo: String,
    val descripcion: String,
    val ejercicios: List<ejercicio>
)

data class ejercicio(
    val nombre: String,
    val series: Int,
    val repeticiones: Int,
    val descansoSegundos: Int
)