package edu.ucne.atlaspath.presentation.tareas.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.atlaspath.data.remote.ExerciseDbApi
import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.domain.model.Ejercicio
import edu.ucne.atlaspath.domain.model.Rutina
import edu.ucne.atlaspath.domain.useCase.GetRutinaDetailUseCase
import edu.ucne.atlaspath.domain.useCase.SaveRutinaUseCase
import edu.ucne.atlaspath.presentation.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailRutinaViewModel @Inject constructor(
    private val getRutinaDetailUseCase: GetRutinaDetailUseCase,
    private val saveRutinaUseCase: SaveRutinaUseCase,
    private val exerciseApi: ExerciseDbApi,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(DetailRutinaUiState())
    val state = _state.asStateFlow()

    private val diccionarioBusqueda = mapOf(
        "pecho" to "chest",
        "espalda" to "back",
        "pierna" to "leg",
        "piernas" to "upper legs",
        "brazo" to "arm",
        "brazos" to "arms",
        "hombro" to "shoulder",
        "hombros" to "shoulders",
        "abdomen" to "waist",
        "core" to "waist",
        "gluteo" to "glutes",
        "glúteo" to "glutes",
        "bicep" to "biceps",
        "tricep" to "triceps",
        "pantorrilla" to "calves",
        "pantorrillas" to "calves"
    )

    private val diccionarioVista = mapOf(
        "chest" to "Pecho",
        "back" to "Espalda",
        "lower legs" to "Pantorrillas",
        "upper legs" to "Piernas (Muslos)",
        "upper arms" to "Bíceps/Tríceps",
        "lower arms" to "Antebrazos",
        "shoulders" to "Hombros",
        "waist" to "Abdomen/Core",
        "glutes" to "Glúteos",
        "cardio" to "Cardio",
        "neck" to "Cuello"
    )

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
                    buscarEnApi(event.query)
                } else {
                    _state.update { it.copy(searchResults = emptyList()) }
                }
            }

            is DetailRutinaEvent.AddEjercicio -> {
                val musculoIngles = event.exerciseDto.target?.lowercase() ?: ""
                val musculoEspanol = diccionarioVista[musculoIngles] ?: event.exerciseDto.target ?: "General"

                val nuevoEjercicio = Ejercicio(
                    nombre = event.exerciseDto.name.replaceFirstChar { it.uppercase() },
                    series = event.series,
                    repeticiones = event.reps,
                    descansoSegundos = event.descanso,
                    grupoMuscular = musculoEspanol,
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
                    grupoMuscular = event.musculo,
                    gifUrl = "",
                    instrucciones = listOf("Añadido rápidamente")
                )
                _state.update { it.copy(
                    ejercicios = it.ejercicios + nuevoEjercicio,
                    searchQuery = "",
                    searchResults = emptyList()
                ) }
            }

            is DetailRutinaEvent.RemoveEjercicio -> {
                val nuevaLista = _state.value.ejercicios.toMutableList().apply { removeAt(event.index) }
                _state.update { it.copy(ejercicios = nuevaLista) }
            }

            DetailRutinaEvent.SaveRutina -> validateAndSave()
        }
    }

    private fun buscarEnApi(queryOriginal: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSearching = true) }
            try {
                var queryTraducida = queryOriginal.lowercase()
                diccionarioBusqueda.forEach { (esp, eng) ->
                    if (queryTraducida.contains(esp)) {
                        queryTraducida = eng
                    }
                }
                val results = exerciseApi.getExerciseByName(name = queryTraducida, limit = 20)
                val translatedResults = results.map { dto ->
                    val musculoOriginal = dto.target?.lowercase()
                    dto.copy(
                        target = diccionarioVista[musculoOriginal] ?: dto.target
                    )
                }

                _state.update { it.copy(searchResults = translatedResults, isSearching = false) }
            } catch (e: Exception) {
                _state.update { it.copy(searchResults = emptyList(), isSearching = false) }
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
            val result = saveRutinaUseCase(rutinaAGuardar)
            when (result) {
                is Resource.Success -> _state.update { it.copy(isLoading = false, success = true) }
                is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                is Resource.Loading -> { }
            }
        }
    }
}