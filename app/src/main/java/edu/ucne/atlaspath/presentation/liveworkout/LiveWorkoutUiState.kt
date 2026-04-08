package edu.ucne.atlaspath.presentation.liveworkout

import edu.ucne.atlaspath.domain.model.Ejercicio

data class ActiveSet(
    val setId: Int,
    val reps: String = "",
    val weightLbs: String = "",
    val isCompleted: Boolean = false
)

data class ActiveExercise(
    val ejercicio: Ejercicio,
    val sets: List<ActiveSet>
)

data class LiveWorkoutUiState(
    val isLoading: Boolean = true,
    val rutinaTitulo: String = "",
    val rutinaId: Int = 0,
    val activeExercises: List<ActiveExercise> = emptyList(),
    val cronometroTotal: Long = 0L,
    val entrenamientoFinalizado: Boolean = false
)