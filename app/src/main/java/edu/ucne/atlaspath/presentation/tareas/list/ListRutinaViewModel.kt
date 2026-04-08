package edu.ucne.atlaspath.presentation.tareas.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.domain.useCase.GetRutinasUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListRutinaViewModel @Inject constructor(
    private val getRutinasUseCase: GetRutinasUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ListRutinaUiState())
    val state = _state.asStateFlow()

    init {
        loadRutinas()
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