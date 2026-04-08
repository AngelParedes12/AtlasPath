package edu.ucne.atlaspath.data.repository

import edu.ucne.atlaspath.data.local.dao.SessionDao
import edu.ucne.atlaspath.data.local.dao.RoutineDao
import edu.ucne.atlaspath.data.local.mapper.toDomain
import edu.ucne.atlaspath.data.local.mapper.toEntity
import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.domain.model.Rutina
import edu.ucne.atlaspath.domain.model.Sesion
import edu.ucne.atlaspath.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RoutineRepositoryImpl @Inject constructor(
    private val routineDao: RoutineDao,
    private val sessionDao: SessionDao
) : RoutineRepository {

    override fun getRoutines(query: String?): Flow<Resource<List<Rutina>>> = flow {
        emit(Resource.Loading())
        try {
            routineDao.getAll(query).collect { entities ->
                emit(Resource.Success(entities.map { it.toDomain() }))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error al cargar rutinas locales"))
        }
    }

    override fun getRoutineById(id: Int): Flow<Resource<Rutina>> = flow {
        emit(Resource.Loading())
        try {
            val entity = routineDao.find(id)
            if (entity != null) {
                emit(Resource.Success(entity.toDomain()))
            } else {
                emit(Resource.Error("Rutina no encontrada"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error al buscar rutina"))
        }
    }

    override suspend fun saveRutina(rutina: Rutina): Resource<Unit> {
        return try {
            routineDao.save(rutina.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al guardar la rutina")
        }
    }

    override suspend fun deleteRutina(id: Int): Resource<Unit> {
        return try {
            routineDao.deleteById(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al eliminar la rutina")
        }
    }

    override suspend fun saveSesion(sesion: Sesion): Resource<Unit> {
        return try {
            sessionDao.save(sesion.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al guardar la sesión")
        }
    }

    override fun getAllSessions(): Flow<Resource<List<Sesion>>> = flow {
        emit(Resource.Loading())
        try {
            sessionDao.getAllSessions().collect { entities ->
                emit(Resource.Success(entities.map { it.toDomain() }))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error al cargar el historial"))
        }
    }
}