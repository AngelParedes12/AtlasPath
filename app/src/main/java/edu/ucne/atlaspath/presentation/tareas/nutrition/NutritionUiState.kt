package edu.ucne.atlaspath.presentation.tareas.nutrition

import edu.ucne.atlaspath.data.remote.dto.RecipeDto
import edu.ucne.atlaspath.domain.model.RegistroNutricional

data class NutritionUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val foodInputText: String = "",
    val dailyRecords: List<RegistroNutricional> = emptyList(),
    val totalCalories: Int = 0,
    val totalProtein: Float = 0f,
    val totalCarbs: Float = 0f,
    val totalFat: Float = 0f,
    val dailyCalorieGoal: Int = 2500,
    val recipeInputText: String = "",
    val isGeneratingRecipe: Boolean = false,
    val generatedRecipe: RecipeDto? = null
)