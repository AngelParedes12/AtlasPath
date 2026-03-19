 package edu.ucne.atlaspath.data.remote

    import edu.ucne.atlaspath.data.remote.dto.GeminiRequest
    import edu.ucne.atlaspath.data.remote.dto.RoutineDto
    import retrofit2.Response
    import retrofit2.http.Body
    import retrofit2.http.POST
    import retrofit2.http.Query

    interface ApiApp {
        // Usamos v1beta para forzar JSON responseMimeType más adelante en el interceptor o config
        @POST("v1beta/models/gemini-2.5-flash-preview-09-2025:generateContent")
        suspend fun generateWorkoutRoutine(
            @Query("key") apiKey: String,
            @Body request: GeminiRequest
        ): Response<RoutineDto> // Moshi parseará el JSON directamente a RoutineDto
    }