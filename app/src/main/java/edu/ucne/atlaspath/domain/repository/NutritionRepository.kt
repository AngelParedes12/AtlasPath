package edu.ucne.atlaspath.domain.repository

import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.data.remote.dto.RecipeDto
import edu.ucne.atlaspath.domain.model.RegistroNutricional
import kotlinx.coroutines.flow.Flow

interface NutritionRepository {
    fun getDailyRecords(dateString: String): Flow<List<RegistroNutricional>>
    suspend fun analyzeAndSaveFood(foodPrompt: String): Resource<RegistroNutricional>
    suspend fun deleteRecord(id: Int): Resource<Unit>
    suspend fun generateRecipe(ingredientsPrompt: String): Resource<RecipeDto>
}