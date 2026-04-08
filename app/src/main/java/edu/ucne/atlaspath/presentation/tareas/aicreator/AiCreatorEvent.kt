package edu.ucne.atlaspath.presentation.tareas.aicreator

sealed interface AiCreatorEvent {
    data class OnPromptChange(val prompt: String) : AiCreatorEvent
    data object GenerateRoutine : AiCreatorEvent
    data object SaveGeneratedRoutine : AiCreatorEvent
    data object DiscardRoutine : AiCreatorEvent
}