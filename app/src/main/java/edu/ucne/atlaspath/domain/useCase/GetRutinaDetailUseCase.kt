package edu.ucne.atlaspath.domain.useCase

import edu.ucne.atlaspath.domain.repository.RoutineRepository
import javax.inject.Inject

class GetRutinaDetailUseCase @Inject constructor(
    private val repository: RoutineRepository
) {
    operator fun invoke(id: Int) = repository.getRoutineById(id)
}