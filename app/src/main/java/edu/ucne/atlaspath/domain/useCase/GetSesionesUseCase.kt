package edu.ucne.atlaspath.domain.useCase

import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.domain.model.Sesion
import edu.ucne.atlaspath.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSesionesUseCase @Inject constructor(
    private val repository: RoutineRepository
) {
    operator fun invoke(): Flow<Resource<List<Sesion>>> {
        return repository.getAllSessions()
    }
}