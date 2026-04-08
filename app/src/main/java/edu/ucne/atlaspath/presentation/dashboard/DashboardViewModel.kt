package edu.ucne.atlaspath.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.atlaspath.data.local.datastore.UserPreferences
import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.domain.useCase.GetDashboardStatsUseCase
import edu.ucne.atlaspath.domain.useCase.GetRutinasUseCase
import edu.ucne.atlaspath.domain.useCase.GetSesionesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val getRutinasUseCase: GetRutinasUseCase,
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase,
    private val getSesionesUseCase: GetSesionesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state = _state.asStateFlow()

    init {
        loadUserData()
        loadRutinaHoy()
        loadStats()
        loadWeeklyCalendar()
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.RefreshData -> {
                loadRutinaHoy()
                loadStats()
            }
            is DashboardEvent.UpdatePeso -> {
                viewModelScope.launch {
                    _state.update { it.copy(pesoActualLbs = event.nuevoPeso) }
                    userPreferences.updateWeight(event.nuevoPeso.toFloat())
                }
            }
            is DashboardEvent.UpdateName -> {
                viewModelScope.launch {
                    _state.update { it.copy(userName = event.nuevoNombre) }
                }
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            userPreferences.userName.collectLatest { name ->
                _state.update { it.copy(userName = name) }
            }
        }
        viewModelScope.launch {
            userPreferences.userProfileFlow.collectLatest { profile ->
                _state.update { it.copy(pesoActualLbs = profile.weightLbs.toDouble()) }
            }
        }
    }

    private fun loadRutinaHoy() {
        viewModelScope.launch {
            getRutinasUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        val rutinas = result.data ?: emptyList()
                        val rutinaSugerida = rutinas.firstOrNull()
                        _state.update { it.copy(isLoading = false, rutinaHoy = rutinaSugerida) }
                    }
                    is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            getDashboardStatsUseCase().collect { result ->
                if (result is Resource.Success) {
                    val stats = result.data
                    if (stats != null) {
                        _state.update {
                            it.copy(
                                rachaDias = stats.rachaDias,
                                totalEntrenamientos = stats.totalEntrenamientos,
                                volumenTotalLbs = stats.volumenTotalLbs,
                                nivelActual = stats.nivelActual,
                                progresoNivel = stats.progresoNivel,
                                rangosMusculares = stats.rangosMusculares
                            )
                        }
                    }
                }
            }
        }
    }
    private fun loadWeeklyCalendar() {
        viewModelScope.launch {
            getSesionesUseCase().collect { result ->
                if (result is Resource.Success) {
                    val sesiones = result.data ?: emptyList()
                    val booleanList = MutableList(7) { false }

                    val cal = Calendar.getInstance()
                    cal.firstDayOfWeek = Calendar.MONDAY
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    val startOfWeek = cal.timeInMillis
                    sesiones.forEach { sesion ->
                        if (sesion.fechaInicio >= startOfWeek) {
                            val sessionCal = Calendar.getInstance().apply { timeInMillis = sesion.fechaInicio }
                            val dayOfWeek = sessionCal.get(Calendar.DAY_OF_WEEK)
                            val index = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2
                            if (index in 0..6) {
                                booleanList[index] = true
                            }
                        }
                    }
                    _state.update { it.copy(diasEntrenadosSemana = booleanList) }
                }
            }
        }
    }
}