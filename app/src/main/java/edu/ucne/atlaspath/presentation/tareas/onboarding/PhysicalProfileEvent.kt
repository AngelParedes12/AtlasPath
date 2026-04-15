package edu.ucne.atlaspath.presentation.tareas.onboarding

sealed class PhysicalProfileEvent {
    data class OnAgeChange(val age: Float) : PhysicalProfileEvent()
    data class OnWeightChange(val weight: Float) : PhysicalProfileEvent()
    data object ToggleWeightUnit : PhysicalProfileEvent()
    data class OnHeightChange(val height: Float) : PhysicalProfileEvent()
    data object ToggleHeightUnit : PhysicalProfileEvent()
    data class OnGenderChange(val gender: String) : PhysicalProfileEvent()
    data class OnLevelChange(val level: String) : PhysicalProfileEvent()
    data class OnSomatotypeChange(val somatotype: String) : PhysicalProfileEvent()
    data class OnGoalChange(val goal: String) : PhysicalProfileEvent()
    data class ToggleHelp(val show: Boolean) : PhysicalProfileEvent()
    data class SaveProfile(val onComplete: () -> Unit) : PhysicalProfileEvent()
}