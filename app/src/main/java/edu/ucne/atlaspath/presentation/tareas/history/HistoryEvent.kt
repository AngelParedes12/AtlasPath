package edu.ucne.atlaspath.presentation.tareas.history

sealed interface HistoryEvent {
    data object Refresh : HistoryEvent
}