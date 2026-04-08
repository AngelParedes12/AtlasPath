package edu.ucne.atlaspath.data.remote

import edu.ucne.atlaspath.data.remote.dto.ExerciseDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ExerciseDbApi {
    @GET("exercises")
    suspend fun getAllExercises(
        @Query("limit") limit: Int = 50,
        @Header("X-RapidAPI-Key") apiKey: String = "llaves de Api",
        @Header("X-RapidAPI-Host") apiHost: String = "exercisedb.p.rapidapi.com"
    ): List<ExerciseDto>

    @GET("exercises/name/{name}")
    suspend fun getExerciseByName(
        @Path("name") name: String,
        @Query("limit") limit: Int = 50,
        @Header("X-RapidAPI-Key") apiKey: String = "",
        @Header("X-RapidAPI-Host") apiHost: String = "exercisedb.p.rapidapi.com"
    ): List<ExerciseDto>

    @GET("exercises/bodyPart/{bodyPart}")
    suspend fun getExercisesByBodyPart(
        @Path("bodyPart") bodyPart: String,
        @Query("limit") limit: Int = 50,
        @Header("X-RapidAPI-Key") apiKey: String = "",
        @Header("X-RapidAPI-Host") apiHost: String = "exercisedb.p.rapidapi.com"
    ): List<ExerciseDto>
}