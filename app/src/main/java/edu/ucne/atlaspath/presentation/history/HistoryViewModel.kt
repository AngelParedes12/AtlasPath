package edu.ucne.atlaspath.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.domain.model.sesion
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

    private fun loadHistory() {
        viewModelScope.launch {
            getSesionesUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        val listaSesiones = result.data ?: emptyList()
                        val volumen = listaSesiones.sumOf { it.volumenTotalLbs }
                        val xp = listaSesiones.sumOf { it.xpGanada }

                        _state.update { it.copy(
                            isLoading = false,
                            sesiones = listaSesiones.reversed(),
                            volumenTotalHistorico = volumen,
                            xpAcumulada = xp,
                            error = null
                        ) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(
                            isLoading = false,
                            error = result.message
                        ) }
                    }
                }
            }
        }
    }
}