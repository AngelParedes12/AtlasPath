package edu.ucne.atlaspath.data.repository

import edu.ucne.atlaspath.data.local.dao.FoodDao
import edu.ucne.atlaspath.data.local.mapper.toDomain
import edu.ucne.atlaspath.data.local.mapper.toEntity
import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.data.remote.dto.RecipeDto
import edu.ucne.atlaspath.data.remote.remoteDataSource.GeminiRemoteDataSource
import edu.ucne.atlaspath.domain.model.RegistroNutricional
import edu.ucne.atlaspath.domain.repository.NutritionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NutritionRepositoryImpl @Inject constructor(
    private val dao: FoodDao,
    private val aiDataSource: GeminiRemoteDataSource
) : NutritionRepository {

    override fun getDailyRecords(dateString: String): Flow<List<RegistroNutricional>> {
        return dao.getRecordsByDate(dateString).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun analyzeAndSaveFood(foodPrompt: String): Resource<RegistroNutricional> {
        return try {
            val result = aiDataSource.analyzeFood(foodPrompt)

            if (result.isSuccess) {
                val nutritionData = result.getOrThrow()
                dao.insertRecord(nutritionData.toEntity())
                Resource.Success(nutritionData)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Error")
            }
        } catch (e: Exception) {
            Resource.Error("Excepción de red: ${e.message}")
        }
    }

    override suspend fun deleteRecord(id: Int): Resource<Unit> {
        return try {
            dao.deleteRecord(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Error al eliminar el registro: ${e.message}")
        }
    }

    override suspend fun generateRecipe(ingredientsPrompt: String): Resource<RecipeDto> {
        return try {
            val result = aiDataSource.generateRecipe(ingredientsPrompt)

            if (result.isSuccess) {
                Resource.Success(result.getOrThrow())
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Error")
            }
        } catch (e: Exception) {
            Resource.Error("Excepción de red: ${e.message}")
        }
    }
}