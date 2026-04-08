package edu.ucne.atlaspath.presentation.tareas.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable data object Onboarding : Screen()
    @Serializable data object PhysicalProfile : Screen()
    @Serializable data object Dashboard : Screen()
    @Serializable data object
    RutinaList : Screen()
    @Serializable data class RutinaDetail(val id: Int) : Screen()
    @Serializable data object AiCreator : Screen()
    @Serializable data class LiveWorkout(val id: Int) : Screen()
    @Serializable data object ExerciseExplorer : Screen()

    @Serializable data object History : Screen()
    @Serializable data object FullCalendar : Screen()
}