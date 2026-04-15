package edu.ucne.atlaspath.presentation.tareas.onboarding

data class PhysicalProfileUiState(
    val age: Float = 25f,
    val weightValue: Float = 70f,
    val isKg: Boolean = true,
    val heightValue: Float = 170f,
    val isCm: Boolean = true,
    val selectedGender: String = "",
    val selectedLevel: String = "",
    val selectedSomatotype: String = "",
    val selectedGoal: String = "",
    val showValidationError: Boolean = false,
    val showSomatotypeHelp: Boolean = false
)