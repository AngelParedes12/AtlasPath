package edu.ucne.atlaspath.domain.useCase

import edu.ucne.atlaspath.data.local.dao.SessionDao
import edu.ucne.atlaspath.data.local.entity.SessionEntity
import edu.ucne.atlaspath.data.remote.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetHistorialUseCase @Inject constructor(
    private val sessionDao: SessionDao
) {
    operator fun invoke(): Flow<Resource<List<SessionEntity>>> {
        return sessionDao.getAllSessions()
            .map { sesiones -> Resource.Success(sesiones) as Resource<List<SessionEntity>> }
            .onStart { emit(Resource.Loading()) }
            .catch { emit(Resource.Error(it.message ?: "Error al cargar historial")) }
    }
}