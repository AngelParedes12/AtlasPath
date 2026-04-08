package edu.ucne.atlaspath.domain.repository

import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.domain.model.Rutina
import edu.ucne.atlaspath.domain.model.sesion
import kotlinx.coroutines.flow.Flow

interface RoutineRepository {
        fun getRoutines(query: String? = null): Flow<Resource<List<Rutina>>>

        fun getRoutineById(id: Int): Flow<Resource<Rutina>>
        suspend fun saveRutina(rutina: Rutina): Resource<Unit>
        suspend fun deleteRutina(id: Int): Resource<Unit>
        suspend fun saveSesion(sesion: sesion): Resource<Unit>
        fun getAllSessions(): Flow<Resource<List<sesion>>>
}