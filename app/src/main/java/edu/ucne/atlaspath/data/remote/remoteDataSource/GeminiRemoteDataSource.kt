package edu.ucne.atlaspath.data.remote.remoteDataSource

import com.squareup.moshi.Moshi
import edu.ucne.atlaspath.BuildConfig
import edu.ucne.atlaspath.data.remote.GeminiApi
import edu.ucne.atlaspath.data.remote.dto.*
import edu.ucne.atlaspath.domain.model.RegistroNutricional
import edu.ucne.atlaspath.domain.model.Rutina
import javax.inject.Inject

class GeminiRemoteDataSource @Inject constructor(
    private val api: GeminiApi,
    private val moshi: Moshi
) {
    private val apiKey = BuildConfig.GEMINI_API_KEY

    suspend fun generateWorkout(userPrompt: String): Result<Rutina> {
        return try {
            val systemInstruction = """
                Eres un entrenador personal experto. 
                Genera una rutina de entrenamiento basada en la siguiente solicitud: "$userPrompt".
                DEBES responder ÚNICAMENTE con un objeto JSON válido con esta estructura exacta:
                {
                  "titulo": "String",
                  "descripcion": "String",
                  "ejercicios": [
                    { 
                      "nombre": "String", 
                      "series": Int, 
                      "repeticiones": Int, 
                      "descansoSegundos": Int,
                      "grupoMuscular": "String"
                    }
                  ]
                }
            """.trimIndent()

            val request = GeminiRequest(contents = listOf(Content(listOf(Part(text = systemInstruction)))))
            val response = api.generateWorkoutRoutine(apiKey, request)

            if (response.isSuccessful) {
                val jsonText = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (jsonText != null) {
                    val cleanJson = jsonText.replace("```json", "").replace("```", "").trim()
                    val adapter = moshi.adapter(RoutineDto::class.java)
                    val routineDto = adapter.fromJson(cleanJson)

                    if (routineDto != null) Result.success(routineDto.toDomain())
                    else Result.failure(Exception("Error al parsear la rutina de la IA"))
                } else {
                    Result.failure(Exception("La IA devolvió una respuesta vacía"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception("HTTP ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Excepción: ${e.message}"))
        }
    }

    suspend fun analyzeFood(foodPrompt: String): Result<RegistroNutricional> {
        return try {
            val systemInstruction = """
                Eres un nutricionista experto y una base de datos de alimentos.
                El usuario te dirá qué comió: "$foodPrompt".
                Tu tarea es estimar de forma precisa y realista las calorías y macronutrientes totales de esa comida.
                DEBES responder ÚNICAMENTE con un objeto JSON válido con esta estructura exacta (sin texto adicional):
                {
                  "comidaTexto": "String (Un título corto y bonito de lo que comió, máximo 5 palabras)",
                  "calorias": Int,
                  "proteina": Float,
                  "carbohidratos": Float,
                  "grasa": Float
                }
            """.trimIndent()

            val request = GeminiRequest(contents = listOf(Content(listOf(Part(text = systemInstruction)))))
            val response = api.generateWorkoutRoutine(apiKey, request)

            if (response.isSuccessful) {
                val jsonText = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (jsonText != null) {
                    val cleanJson = jsonText.replace("```json", "").replace("```", "").trim()
                    val adapter = moshi.adapter(FoodAnalysisDto::class.java)
                    val foodDto = adapter.fromJson(cleanJson)

                    if (foodDto != null) Result.success(foodDto.toDomain())
                    else Result.failure(Exception("Error al parsear el alimento de la IA"))
                } else {
                    Result.failure(Exception("La IA devolvió una respuesta vacía"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception("HTTP ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Excepción: ${e.message}"))
        }
    }

    suspend fun generateRecipe(ingredientsPrompt: String): Result<RecipeDto> {
        return try {
            val systemInstruction = """
                Eres un Chef experto en nutrición deportiva. 
                El usuario te dirá qué ingredientes tiene o qué se le antoja: "$ingredientsPrompt".
                Tu tarea es crear una receta saludable, fácil de hacer y alta en proteínas con esos datos.
                DEBES responder ÚNICAMENTE con un objeto JSON válido con esta estructura exacta:
                {
                  "titulo": "String (Nombre atractivo de la receta)",
                  "tiempoPreparacion": "String (Ej: 15 min)",
                  "ingredientes": ["String", "String"],
                  "instrucciones": ["Paso 1...", "Paso 2..."],
                  "caloriasEstimadas": Int,
                  "proteinaEstimada": Float
                }
            """.trimIndent()

            val request = GeminiRequest(contents = listOf(Content(listOf(Part(text = systemInstruction)))))
            val response = api.generateWorkoutRoutine(apiKey, request)

            if (response.isSuccessful) {
                val jsonText = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (jsonText != null) {
                    val cleanJson = jsonText.replace("```json", "").replace("```", "").trim()
                    val adapter = moshi.adapter(RecipeDto::class.java)
                    val recipeDto = adapter.fromJson(cleanJson)

                    if (recipeDto != null) Result.success(recipeDto)
                    else Result.failure(Exception("Error al parsear la receta de la IA"))
                } else {
                    Result.failure(Exception("La IA devolvió una respuesta vacía"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception("HTTP ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Excepción: ${e.message}"))
        }
    }
}