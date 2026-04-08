package edu.ucne.atlaspath.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.atlaspath.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state = _state.asStateFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.OnNombreChange -> _state.update { it.copy(nombre = event.nombre) }
            is OnboardingEvent.OnNivelChange -> _state.update { it.copy(nivel = event.nivel) }
            is OnboardingEvent.OnObjetivoChange -> _state.update { it.copy(objetivo = event.objetivo) }
            OnboardingEvent.FinalizarOnboarding -> guardarPerfil()
        }
    }

    private fun guardarPerfil() {
        val currentState = _state.value
        if (currentState.nombre.isBlank()) {
            _state.update { it.copy(error = "Por favor, dinos tu nombre") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                userPreferences.saveUserName(currentState.nombre)
                _state.update { it.copy(isLoading = false, success = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}