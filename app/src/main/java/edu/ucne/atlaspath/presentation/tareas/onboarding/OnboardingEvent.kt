package edu.ucne.atlaspath.presentation.tareas.onboarding

sealed interface OnboardingEvent {
    data class OnNombreChange(val nombre: String) : OnboardingEvent
    data class OnNivelChange(val nivel: String) : OnboardingEvent
    data class OnObjetivoChange(val objetivo: String) : OnboardingEvent
    data object FinalizarOnboarding : OnboardingEvent
}