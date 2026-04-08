package edu.ucne.atlaspath.presentation.tareas.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.domain.useCase.GetSesionesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getSesionesUseCase: GetSesionesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryUiState())
    val state = _state.asStateFlow()

    init {
        loadHistory()
    }

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.Refresh -> loadHistory()
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            getSesionesUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val sesiones = result.data ?: emptyList()
                        val xpTotal = sesiones.sumOf { it.xpGanada }
                        val volumenTotal = sesiones.sumOf { it.volumenTotalLbs }

                        _state.update {
                            it.copy(
                                isLoading = false,
                                sesiones = sesiones,
                                xpAcumulada = xpTotal,
                                volumenTotalHistorico = volumenTotal
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }
}