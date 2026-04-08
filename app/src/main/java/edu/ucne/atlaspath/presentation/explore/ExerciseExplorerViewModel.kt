package edu.ucne.atlaspath.presentation.explorer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.atlaspath.data.remote.ExerciseDbApi
import edu.ucne.atlaspath.data.remote.dto.ExerciseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExplorerUiState(
    val isLoading: Boolean = false,
    val exercises: List<ExerciseDto> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
)

sealed interface ExplorerEvent {
    data class OnSearchQueryChange(val query: String) : ExplorerEvent
    data object Search : ExplorerEvent
    data class FilterByMuscle(val muscle: String) : ExplorerEvent
}

@HiltViewModel
class ExerciseExplorerViewModel @Inject constructor(
    private val api: ExerciseDbApi
) : ViewModel() {

    private val _state = MutableStateFlow(ExplorerUiState())
    val state = _state.asStateFlow()

    init {
        loadInitialExercises()
    }

    fun onEvent(event: ExplorerEvent) {
        when (event) {
            is ExplorerEvent.OnSearchQueryChange -> _state.update { it.copy(searchQuery = event.query) }
            ExplorerEvent.Search -> searchExercises(_state.value.searchQuery)
            is ExplorerEvent.FilterByMuscle -> filterExercises(event.muscle)
        }
    }

    private fun loadInitialExercises() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val result = api.getAllExercises(limit = 20)
                _state.update { it.copy(isLoading = false, exercises = result) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun searchExercises(query: String) {
        if (query.isBlank()) {
            loadInitialExercises()
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val result = api.getExerciseByName(name = query.lowercase(), limit = 30)
                _state.update { it.copy(isLoading = false, exercises = result) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "No se encontraron ejercicios.") }
            }
        }
    }

    private fun filterExercises(muscle: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, searchQuery = "") }
            try {
                val result = api.getExercisesByBodyPart(bodyPart = muscle.lowercase(), limit = 30)
                _state.update { it.copy(isLoading = false, exercises = result) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}