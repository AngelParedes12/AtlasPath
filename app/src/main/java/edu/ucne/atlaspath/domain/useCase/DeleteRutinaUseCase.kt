package edu.ucne.atlaspath.domain.useCase

import edu.ucne.atlaspath.domain.repository.RoutineRepository
import javax.inject.Inject

class DeleteRutinaUseCase @Inject constructor(
    private val repository: RoutineRepository
) {
    suspend operator fun invoke(id: Int) = repository.deleteRutina(id)
}