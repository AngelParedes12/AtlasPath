package edu.ucne.atlaspath.presentation.liveworkout

sealed class LiveWorkoutEvent {
    data class UpdateSetValues(val exIndex: Int, val setIndex: Int, val weight: String, val reps: String) : LiveWorkoutEvent()
    data class ToggleSetComplete(val exIndex: Int, val setIndex: Int) : LiveWorkoutEvent()
    object FinalizarEntrenamiento : LiveWorkoutEvent()
}