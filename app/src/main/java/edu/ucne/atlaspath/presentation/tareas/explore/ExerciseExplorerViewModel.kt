package edu.ucne.atlaspath.presentation.tareas.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.atlaspath.data.remote.dto.ExerciseDto // Seguimos usando el DTO para la UI temporalmente para no romper la pantalla
import edu.ucne.atlaspath.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseExplorerViewModel @Inject constructor(
    private val repository: RoutineRepository
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
            repository.getAllExercisesLocal().collect { entities ->
                val dtos = entities.map { ExerciseDto(it.id, it.name, it.bodyPart, it.equipment, it.target, it.gifUrl, emptyList()) }
                _state.update { it.copy(isLoading = false, exercises = dtos) }
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
            repository.searchExercisesLocal(query).collect { entities ->
                val dtos = entities.map { ExerciseDto(it.id, it.name, it.bodyPart, it.equipment, it.target, it.gifUrl, emptyList()) }
                _state.update { it.copy(isLoading = false, exercises = dtos) }
            }
        }
    }

    private fun filterExercises(muscle: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, searchQuery = "") }
            repository.getExercisesByMuscleLocal(muscle).collect { entities ->
                val dtos = entities.map { ExerciseDto(it.id, it.name, it.bodyPart, it.equipment, it.target, it.gifUrl, emptyList()) }
                _state.update { it.copy(isLoading = false, exercises = dtos) }
            }
        }
    }
}