package edu.ucne.atlaspath.data.remote.remoteDatasource

import edu.ucne.atlaspath.data.remote.ApiApp
import edu.ucne.atlaspath.data.remote.dto.GeminiRequest
import edu.ucne.atlaspath.data.remote.dto.RoutineDto
import edu.ucne.atlaspath.BuildConfig // Asegúrate de tener la API KEY en build.gradle
import javax.inject.Inject

class GeminiRemoteDataSource @Inject constructor(
    private val api: ApiApp
) {
    suspend fun generateRoutine(prompt: String): Result<RoutineDto> {
        return try {
            // Construimos el request formatado para Gemini
            val request = GeminiRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt))))
            )

            val response = api.generateWorkoutRoutine(BuildConfig.GEMINI_API_KEY, request)

            if (response.isSuccessful) {
                Result.success(response.body() ?: RoutineDto())
            } else {
                Result.failure(Exception("Error de Gemini: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}