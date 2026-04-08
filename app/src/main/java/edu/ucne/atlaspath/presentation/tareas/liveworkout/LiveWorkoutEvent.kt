package edu.ucne.atlaspath.presentation.tareas.liveworkout

sealed interface LiveWorkoutEvent {
    data class UpdateSetValues(val exIndex: Int, val setIndex: Int, val weight: String, val reps: String) : LiveWorkoutEvent
    data class ToggleSetComplete(val exIndex: Int, val setIndex: Int) : LiveWorkoutEvent
    data object FinalizarEntrenamiento : LiveWorkoutEvent
}