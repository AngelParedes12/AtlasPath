package edu.ucne.atlaspath.presentation.tareas.aicreator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.atlaspath.data.local.datastore.UserPreferences
import edu.ucne.atlaspath.data.remote.remoteDataSource.GeminiRemoteDataSource
import edu.ucne.atlaspath.domain.useCase.SaveRutinaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiCreatorViewModel @Inject constructor(
    private val geminiDataSource: GeminiRemoteDataSource,
    private val saveRutinaUseCase: SaveRutinaUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(AiCreatorUiState())
    val state = _state.asStateFlow()

    fun onEvent(event: AiCreatorEvent) {
        when (event) {
            is AiCreatorEvent.OnPromptChange -> _state.update { it.copy(prompt = event.prompt, error = null) }
            AiCreatorEvent.GenerateRoutine -> generateRoutine()
            AiCreatorEvent.SaveGeneratedRoutine -> saveRoutine()
            AiCreatorEvent.DiscardRoutine -> _state.update { it.copy(rutinaGenerada = null, prompt = "") }
        }
    }

    private fun generateRoutine() {
        val currentPrompt = _state.value.prompt
        if (currentPrompt.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {

                val profile = userPreferences.userProfileFlow.first()
                val enhancedPrompt = """
                    Actúa como un entrenador personal experto de clase mundial.
                    Diseña una rutina basándote ESTRICTAMENTE en la siguiente petición: "$currentPrompt"
                    
                    ADAPTACIÓN BIOMÉTRICA OBLIGATORIA:
                    Ajusta la selección de ejercicios, series, repeticiones y tiempo de descanso basándote en este perfil del cliente:
                    - Edad: ${profile.age} años
                    - Peso: ${profile.weightLbs} lbs
                    - Altura: ${profile.heightCm} cm
                    - Somatotipo (Genética): ${profile.somatotype}
                    - Objetivo Principal: ${profile.goal}
                    
                    Nota para la IA: Si el objetivo es pérdida de grasa o es endomorfo, considera descansos más cortos o mayor densidad de trabajo. Si es ganancia muscular o ectomorfo, prioriza hipertrofia pesada y descansos más largos.
                """.trimIndent()

                val result = geminiDataSource.generateWorkout(enhancedPrompt)

                result.onSuccess { rutina ->
                    _state.update { it.copy(isLoading = false, rutinaGenerada = rutina) }
                }.onFailure { exception ->
                    _state.update { it.copy(isLoading = false, error = exception.message) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Error al leer el perfil o contactar a la IA.") }
            }
        }
    }

    private fun saveRoutine() {
        val rutina = _state.value.rutinaGenerada ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            saveRutinaUseCase(rutina)
            _state.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}