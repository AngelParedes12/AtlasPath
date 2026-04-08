package edu.ucne.atlaspath.presentation.tareas.rutina.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.domain.useCase.GetRutinasUseCase
import edu.ucne.atlaspath.domain.useCase.DeleteRutinaUseCase // <-- Importado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListRutinaViewModel @Inject constructor(
    private val getRutinasUseCase: GetRutinasUseCase,
    private val deleteRutinaUseCase: DeleteRutinaUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ListRutinaUiState())
    val state = _state.asStateFlow()

    init {
        loadRutinas()
    }

    fun onEvent(event: ListRutinaEvent) {
        when(event) {
            is ListRutinaEvent.DeleteRutina -> deleteRutina(event.id)
            is ListRutinaEvent.OnSearchQueryChange -> {
            }
        }
    }

    private fun deleteRutina(id: Int) {
        viewModelScope.launch {
            deleteRutinaUseCase(id)
        }
    }

    private fun loadRutinas() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getRutinasUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> _state.update { it.copy(isLoading = false, rutinas = result.data ?: emptyList()) }
                    is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }
}