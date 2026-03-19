package edu.ucne.atlaspath.data.repository

import edu.ucne.atlaspath.data.local.dao.RoutineDao
import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.data.remote.remoteDatasource.GeminiRemoteDataSource
import edu.ucne.atlaspath.domain.model.rutina
import edu.ucne.atlaspath.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoutineRepositoryImpl @Inject constructor(
    private val routineDao: RoutineDao,
    private val geminiDataSource: GeminiRemoteDataSource
) : RoutineRepository {

    // 1. Single Source of Truth: Exponemos el Flow de Room envuelto en tu Resource
    override fun observeRoutines(): Flow<Resource<List<rutina>>> = flow {
        emit(Resource.Loading())
        try {
            routineDao.observeAllRoutines()
                .map { entities ->
                    entities.map { it.toDomain() }
                }
                .collect { rutinas ->
                    emit(Resource.Success(rutinas))
                }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error al cargar rutinas locales"))
        }
    }

    // 2. Offline-First: Llamamos a IA, guardamos local. No retornamos la rutina.
    override suspend fun generateAndSaveAiRoutine(prompt: String): Resource<Unit> {
        // Podríamos emitir Loading aquí si usáramos Flow, pero como es suspend,
        // el ViewModel manejará el estado de carga antes de llamar a esto.

        val result = geminiDataSource.generateRoutine(prompt)

        return if (result.isSuccess) {
            val routineDto = result.getOrNull()
            if (routineDto != null) {
                // Mapeamos a Entidad e Insertamos en Room
                // Esto disparará automáticamente el Flow en observeRoutines()
                routineDao.insertRoutine(routineDto.toEntity())
                Resource.Success(Unit)
            } else {
                Resource.Error("Gemini devolvió una rutina vacía")
            }
        } else {
            Resource.Error(result.exceptionOrNull()?.message ?: "Error desconocido en la IA")
        }
    }
}