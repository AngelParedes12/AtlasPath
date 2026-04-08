package edu.ucne.atlaspath.data.remote.remoteDataSource

import com.squareup.moshi.Moshi
import edu.ucne.atlaspath.data.remote.GeminiApi
import edu.ucne.atlaspath.data.remote.dto.*
import edu.ucne.atlaspath.domain.model.Rutina
import javax.inject.Inject

class GeminiRemoteDataSource @Inject constructor(
    private val api: GeminiApi,
    private val moshi: Moshi
) {
    suspend fun generateWorkout(userPrompt: String): Result<Rutina> {
        return try {
            val systemInstruction = """
                Eres un entrenador personal experto. 
                Genera una rutina de entrenamiento basada en la siguiente solicitud: "$userPrompt".
                DEBES responder ÚNICAMENTE con un objeto JSON válido con esta estructura exacta:
                {
                  "titulo": "String",
                  "descripción": "String",
                  "ejercicios": [
                    { 
                      "nombre": "String", 
                      "series": Int, 
                      "repeticiones": Int, 
                      "descansoSegundos": Int,
                      "grupoMuscular": "String" // ASIGNA ESTRICTAMENTE UNO DE ESTOS: Pecho, Espalda, Piernas, Brazos, Hombros, Core o Cardio
                    }
                  ]
                }
            """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(Content(listOf(Part(text = systemInstruction))))
            )

            val response = api.generateWorkoutRoutine("", request)

            if (response.isSuccessful) {
                val jsonText = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (jsonText != null) {
                    val cleanJson = jsonText.replace("```json", "").replace("```", "").trim()

                    val adapter = moshi.adapter(RoutineDto::class.java)
                    val routineDto = adapter.fromJson(cleanJson)

                    if (routineDto != null) {
                        Result.success(routineDto.toDomain())
                    } else {
                        Result.failure(Exception("Error al parsear la rutina de la IA"))
                    }
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