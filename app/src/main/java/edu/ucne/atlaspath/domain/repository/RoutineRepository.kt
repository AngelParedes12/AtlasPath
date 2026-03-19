package edu.ucne.atlaspath.domain.repository

import edu.ucne.atlaspath.data.remote.Resource // Usaremos tu clase Resource
import edu.ucne.atlaspath.domain.model.Rutina
import kotlinx.coroutines.flow.Flow

interface RoutineRepository {
    // SSOT: Observar datos locales
    fun observeRoutines(): Flow<Resource<List<Rutina>>>

    // Offline-First: Acción de generar e insertar localmente
    suspend fun generateAndSaveAiRoutine(prompt: String): Resource<Unit>
}