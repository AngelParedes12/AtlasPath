package edu.ucne.atlaspath.presentation.liveworkout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.domain.model.RegistroEjercicio
import edu.ucne.atlaspath.domain.model.sesion
import edu.ucne.atlaspath.domain.useCase.GetRutinaDetailUseCase
import edu.ucne.atlaspath.domain.useCase.SaveSesionUseCase
import edu.ucne.atlaspath.presentation.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.toMutableList

@HiltViewModel
class LiveWorkoutViewModel @Inject constructor(
    private val getRutinaDetailUseCase: GetRutinaDetailUseCase,
    private val saveSesionUseCase: SaveSesionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(LiveWorkoutUiState())
    val state = _state.asStateFlow()

    private var totalTimeJob: Job? = null
    private val startTime = System.currentTimeMillis()

    init {
        val args = savedStateHandle.toRoute<Screen.LiveWorkout>()
        loadRutina(args.id)
        iniciarCronometroTotal()
    }

    private fun loadRutina(id: Int) {
        viewModelScope.launch {
            getRutinaDetailUseCase(id).collect { result ->
                if (result is Resource.Success && result.data != null) {
                    val rutina = result.data
                    val activeExs = rutina.ejercicios.map { ej ->
                        val emptySets = List(ej.series) { index ->
                            ActiveSet(setId = index + 1, reps = ej.repeticiones.toString())
                        }
                        ActiveExercise(ejercicio = ej, sets = emptySets)
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            rutinaId = rutina.rutinaId,
                            rutinaTitulo = rutina.titulo,
                            activeExercises = activeExs
                        )
                    }
                }
            }
        }
    }

    private fun iniciarCronometroTotal() {
        totalTimeJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _state.update { it.copy(cronometroTotal = (System.currentTimeMillis() - startTime) / 1000) }
            }
        }
    }

    fun onEvent(event: LiveWorkoutEvent) {
        when (event) {
            is LiveWorkoutEvent.UpdateSetValues -> {
                val updatedExercises = _state.value.activeExercises.toMutableList()
                val currentExercise = updatedExercises[event.exIndex]
                val updatedSets = currentExercise.sets.toMutableList()

                updatedSets[event.setIndex] = updatedSets[event.setIndex].copy(
                    weightLbs = event.weight,
                    reps = event.reps
                )

                updatedExercises[event.exIndex] = currentExercise.copy(sets = updatedSets)
                _state.update { it.copy(activeExercises = updatedExercises) }
            }

            is LiveWorkoutEvent.ToggleSetComplete -> {
                val updatedExercises = _state.value.activeExercises.toMutableList()
                val currentExercise = updatedExercises[event.exIndex]
                val updatedSets = currentExercise.sets.toMutableList()

                val currentSet = updatedSets[event.setIndex]
                updatedSets[event.setIndex] = currentSet.copy(isCompleted = !currentSet.isCompleted)

                updatedExercises[event.exIndex] = currentExercise.copy(sets = updatedSets)
                _state.update { it.copy(activeExercises = updatedExercises) }
            }

            is LiveWorkoutEvent.FinalizarEntrenamiento -> guardarYTerminar()
        }
    }

    private fun guardarYTerminar() {
        totalTimeJob?.cancel()

        viewModelScope.launch {
            var volumenTotal = 0.0
            val registrosFinales = mutableListOf<RegistroEjercicio>()

            _state.value.activeExercises.forEach { activeEx ->
                activeEx.sets.forEach { set ->
                    val peso = set.weightLbs.toDoubleOrNull() ?: 0.0
                    val reps = set.reps.toIntOrNull() ?: 0

                    if (set.isCompleted || peso > 0.0) {
                        volumenTotal += (peso * reps)

                        registrosFinales.add(
                            RegistroEjercicio(
                                ejercicioNombre = activeEx.ejercicio.nombre,
                                grupoMuscular = activeEx.ejercicio.grupoMuscular,
                                pesoLbs = peso,
                                repeticionesHechas = reps
                            )
                        )
                    }
                }
            }
            val xpTotalGanada = 500 + (volumenTotal * 0.05).toInt()

            val nuevaSesion = sesion(
                rutinaId = _state.value.rutinaId,
                fechaInicio = startTime,
                fechaFin = System.currentTimeMillis(),
                volumenTotalLbs = volumenTotal,
                xpGanada = xpTotalGanada,
                registros = registrosFinales
            )

            try {
                saveSesionUseCase(nuevaSesion)
                _state.update { it.copy(entrenamientoFinalizado = true) }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(entrenamientoFinalizado = true) }
            }
        }
    }
}