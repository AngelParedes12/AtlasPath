package edu.ucne.atlaspath.data.remote

import edu.ucne.atlaspath.data.remote.dto.Content

data class GeminiResponse(
    val candidates: List<Candidate>?
)

data class Candidate(
    val content: Content?
)