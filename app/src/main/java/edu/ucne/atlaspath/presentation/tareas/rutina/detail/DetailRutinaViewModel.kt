package edu.ucne.atlaspath.presentation.tareas.rutina.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.data.remote.dto.ExerciseDto
import edu.ucne.atlaspath.domain.model.Ejercicio
import edu.ucne.atlaspath.domain.model.Rutina
import edu.ucne.atlaspath.domain.repository.RoutineRepository
import edu.ucne.atlaspath.domain.useCase.GetRutinaDetailUseCase
import edu.ucne.atlaspath.domain.useCase.SaveRutinaUseCase
import edu.ucne.atlaspath.presentation.tareas.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailRutinaViewModel @Inject constructor(
    private val getRutinaDetailUseCase: GetRutinaDetailUseCase,
    private val saveRutinaUseCase: SaveRutinaUseCase,
    private val repository: RoutineRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(DetailRutinaUiState())
    val state = _state.asStateFlow()
    private var searchJob: Job? = null

    init {
        val args = savedStateHandle.toRoute<Screen.RutinaDetail>()
        if (args.id > 0) {
            loadRutina(args.id)
        }
    }

    fun onEvent(event: DetailRutinaEvent) {
        when (event) {
            is DetailRutinaEvent.OnTituloChange -> _state.update { it.copy(titulo = event.titulo, tituloError = null) }
            is DetailRutinaEvent.OnDescripcionChange -> _state.update { it.copy(descripcion = event.descripcion) }

            is DetailRutinaEvent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = event.query) }
                if (event.query.length >= 3) {
                    buscarLocalmente(event.query)
                } else {
                    _state.update { it.copy(searchResults = emptyList()) }
                }
            }

            is DetailRutinaEvent.AddEjercicio -> {
                val nuevoEjercicio = Ejercicio(
                    nombre = event.exerciseDto.name,
                    series = event.series,
                    repeticiones = event.reps,
                    descansoSegundos = event.descanso,
                    grupoMuscular = event.exerciseDto.target ?: "General",
                    gifUrl = event.exerciseDto.gifUrl,
                    instrucciones = event.exerciseDto.instructions
                )

                _state.update { it.copy(
                    ejercicios = it.ejercicios + nuevoEjercicio,
                    searchQuery = "",
                    searchResults = emptyList()
                ) }
            }

            is DetailRutinaEvent.AddEjercicioManual -> {
                val nuevoEjercicio = Ejercicio(
                    nombre = event.nombre,
                    series = 4,
                    repeticiones = 10,
                    descansoSegundos = 60,
                    grupoMuscular = event.musculo
                )
                _state.update { it.copy(ejercicios = it.ejercicios + nuevoEjercicio, searchQuery = "", searchResults = emptyList()) }
            }

            is DetailRutinaEvent.RemoveEjercicio -> {
                val nuevaLista = _state.value.ejercicios.toMutableList().apply { removeAt(event.index) }
                _state.update { it.copy(ejercicios = nuevaLista) }
            }

            DetailRutinaEvent.SaveRutina -> validateAndSave()
        }
    }

    private fun buscarLocalmente(query: String) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            _state.update { it.copy(isSearching = true) }
            repository.searchExercisesLocal(query).collect { entities ->
                val dtos = entities.map { ExerciseDto(it.id, it.name, it.bodyPart, it.equipment, it.target, it.gifUrl, emptyList()) }
                _state.update { it.copy(searchResults = dtos, isSearching = false) }
            }
        }
    }

    private fun loadRutina(id: Int) {
        viewModelScope.launch {
            getRutinaDetailUseCase(id).collect { result ->
                if (result is Resource.Success) {
                    val rutina = result.data
                    if (rutina != null) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                rutinaId = rutina.rutinaId,
                                titulo = rutina.titulo,
                                descripcion = rutina.descripcion,
                                ejercicios = rutina.ejercicios
                            )
                        }
                    }
                }
            }
        }
    }

    private fun validateAndSave() {
        val currentState = _state.value
        if (currentState.titulo.isBlank()) {
            _state.update { it.copy(tituloError = "El título no puede estar vacío") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val rutinaAGuardar = Rutina(
                rutinaId = currentState.rutinaId,
                titulo = currentState.titulo,
                descripcion = currentState.descripcion,
                ejercicios = currentState.ejercicios
            )

            when (val result = saveRutinaUseCase(rutinaAGuardar)) {
                is Resource.Success -> _state.update { it.copy(isLoading = false, success = true) }
                is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                is Resource.Loading -> _state.update { it.copy(isLoading = true) }
            }
        }
    }
}