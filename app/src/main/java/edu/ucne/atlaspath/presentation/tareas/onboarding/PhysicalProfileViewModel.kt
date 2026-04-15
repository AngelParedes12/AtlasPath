package edu.ucne.atlaspath.presentation.tareas.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.atlaspath.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhysicalProfileViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(PhysicalProfileUiState())
    val state: StateFlow<PhysicalProfileUiState> = _state.asStateFlow()

    fun onEvent(event: PhysicalProfileEvent) {
        when (event) {
            is PhysicalProfileEvent.OnAgeChange -> _state.update { it.copy(age = event.age) }
            is PhysicalProfileEvent.OnWeightChange -> _state.update { it.copy(weightValue = event.weight) }
            is PhysicalProfileEvent.OnHeightChange -> _state.update { it.copy(heightValue = event.height) }
            is PhysicalProfileEvent.OnGenderChange -> _state.update { it.copy(selectedGender = event.gender, showValidationError = false) }
            is PhysicalProfileEvent.OnLevelChange -> _state.update { it.copy(selectedLevel = event.level, showValidationError = false) }
            is PhysicalProfileEvent.OnSomatotypeChange -> _state.update { it.copy(selectedSomatotype = event.somatotype, showValidationError = false) }
            is PhysicalProfileEvent.OnGoalChange -> _state.update { it.copy(selectedGoal = event.goal, showValidationError = false) }
            is PhysicalProfileEvent.ToggleHelp -> _state.update { it.copy(showSomatotypeHelp = event.show) }
            is PhysicalProfileEvent.ToggleWeightUnit -> toggleWeightUnit()
            is PhysicalProfileEvent.ToggleHeightUnit -> toggleHeightUnit()
            is PhysicalProfileEvent.SaveProfile -> saveProfileData(event.onComplete)
        }
    }

    private fun toggleWeightUnit() {
        _state.update { currentState ->
            val isKgNow = !currentState.isKg
            val newWeight = if (isKgNow) currentState.weightValue / 2.20462f else currentState.weightValue * 2.20462f
            currentState.copy(isKg = isKgNow, weightValue = newWeight)
        }
    }

    private fun toggleHeightUnit() {
        _state.update { currentState ->
            val isCmNow = !currentState.isCm
            val newHeight = if (isCmNow) currentState.heightValue * 30.48f else currentState.heightValue / 30.48f
            currentState.copy(isCm = isCmNow, heightValue = newHeight)
        }
    }

    private fun saveProfileData(onComplete: () -> Unit) {
        val currentState = _state.value
        val valid = currentState.selectedGender.isNotBlank() &&
                currentState.selectedLevel.isNotBlank() &&
                currentState.selectedSomatotype.isNotBlank() &&
                currentState.selectedGoal.isNotBlank()

        if (valid) {
            val finalWeight = if (currentState.isKg) currentState.weightValue * 2.20462f else currentState.weightValue
            val finalHeight = if (!currentState.isCm) currentState.heightValue * 30.48f else currentState.heightValue

            viewModelScope.launch {
                userPreferences.savePhysicalProfile(
                    age = currentState.age.toInt(),
                    weightLbs = finalWeight,
                    heightCm = finalHeight,
                    somatotype = currentState.selectedSomatotype,
                    goal = currentState.selectedGoal,
                    gymLevel = currentState.selectedLevel,
                    gender = currentState.selectedGender
                )
                userPreferences.saveOnboardingCompleted(true)
                onComplete()
            }
        } else {
            _state.update { it.copy(showValidationError = true) }
        }
    }
}