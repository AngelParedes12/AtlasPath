package edu.ucne.atlaspath.presentation.tareas.nutrition

sealed interface NutritionEvent {
    data class OnFoodInputChanged(val text: String) : NutritionEvent
    data object AnalyzeAndSaveFood : NutritionEvent
    data class DeleteRecord(val id: Int) : NutritionEvent
    data class OnRecipeInputChanged(val text: String) : NutritionEvent
    data object GenerateRecipe : NutritionEvent
}