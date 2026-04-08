package edu.ucne.atlaspath.presentation.tareas.detail

import edu.ucne.atlaspath.data.remote.dto.ExerciseDto

sealed interface DetailRutinaEvent {
    data class OnTituloChange(val titulo: String) : DetailRutinaEvent
    data class OnDescripcionChange(val descripcion: String) : DetailRutinaEvent
    data class OnSearchQueryChange(val query: String) : DetailRutinaEvent
    data class AddEjercicio(val exerciseDto: ExerciseDto, val series: Int, val reps: Int, val descanso: Int) : DetailRutinaEvent
    data class AddEjercicioManual(val nombre: String, val musculo: String) : DetailRutinaEvent
    data class RemoveEjercicio(val index: Int) : DetailRutinaEvent
    data object SaveRutina : DetailRutinaEvent
}