package edu.ucne.atlaspath.presentation.tareas.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.atlaspath.data.local.datastore.UserPreferences
import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.domain.useCase.GetDashboardStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state = _state.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            userPreferences.userName.collectLatest { name ->
                _state.update { it.copy(userName = name) }
            }
        }

        viewModelScope.launch {
            getDashboardStatsUseCase().collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { stats ->
                            val rankName = when (stats.nivelActual) {
                                in 1..4 -> "Principiante"
                                in 5..9 -> "Guerrero"
                                in 10..19 -> "Élite"
                                else -> "Leyenda"
                            }
                            _state.update {
                                it.copy(
                                    nivelActual = stats.nivelActual,
                                    rangoNombre = rankName
                                )
                            }
                        }
                    }
                    is Resource.Error -> _state.update { it }
                    is Resource.Loading -> _state.update { it }
                }
            }
        }
    }
}