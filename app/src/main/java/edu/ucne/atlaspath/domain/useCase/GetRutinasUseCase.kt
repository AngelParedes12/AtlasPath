package edu.ucne.atlaspath.domain.useCase

import edu.ucne.atlaspath.domain.repository.RoutineRepository
import javax.inject.Inject

class GetRutinasUseCase @Inject constructor(
    private val repository: RoutineRepository
) {
    operator fun invoke(query: String? = null) = repository.getRoutines(query)
}