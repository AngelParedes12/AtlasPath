package edu.ucne.atlaspath.presentation.tareas.rutina.list

sealed interface ListRutinaEvent {
    data class OnSearchQueryChange(val query: String) : ListRutinaEvent
    data class DeleteRutina(val id: Int) : ListRutinaEvent
}