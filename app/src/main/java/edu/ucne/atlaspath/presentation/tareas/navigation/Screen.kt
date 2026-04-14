package edu.ucne.atlaspath.presentation.tareas.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable data object Onboarding : Screen()
    @Serializable data object PhysicalProfile : Screen()
    @Serializable data object SanctuaryLoading : Screen() // <- Agregado para Fase 3
    @Serializable data object Dashboard : Screen()
    @Serializable data object RutinaList : Screen()
    @Serializable data class RutinaDetail(val id: Int) : Screen()
    @Serializable data object AiCreator : Screen()
    @Serializable data class LiveWorkout(val id: Int) : Screen()
    @Serializable data object History : Screen()
    @Serializable data object ExerciseExplorer : Screen()
    @Serializable data object FullCalendar : Screen()
    @Serializable data class Nutrition(val pesoLbs: Float) : Screen()
    @Serializable data object Profile : Screen()
    @Serializable data object EditProfile : Screen()
}