package edu.ucne.atlaspath.data.remote

import edu.ucne.atlaspath.data.remote.dto.ExerciseDto
import edu.ucne.atlaspath.data.remote.dto.GeminiRequest
import edu.ucne.atlaspath.data.remote.dto.GeminiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ExerciseDbApi {
    @GET("exercises")
    suspend fun getAllExercises(
        @Query("limit") limit: Int = 50
    ): List<ExerciseDto>

    @GET("exercises/name/{name}")
    suspend fun getExerciseByName(
        @Path("name") name: String,
        @Query("limit") limit: Int = 50
    ): List<ExerciseDto>

    @GET("exercises/bodyPart/{bodyPart}")
    suspend fun getExercisesByBodyPart(
        @Path("bodyPart") bodyPart: String,
        @Query("limit") limit: Int = 50
    ): List<ExerciseDto>
}

interface GeminiApi {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateWorkoutRoutine(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}