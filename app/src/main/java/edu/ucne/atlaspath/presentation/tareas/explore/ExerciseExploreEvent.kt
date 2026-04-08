package edu.ucne.atlaspath.presentation.tareas.explore

sealed interface ExplorerEvent {
    data class OnSearchQueryChange(val query: String) : ExplorerEvent
    data object Search : ExplorerEvent
    data class FilterByMuscle(val muscle: String) : ExplorerEvent
}
