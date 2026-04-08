package edu.ucne.atlaspath.domain.useCase

import edu.ucne.atlaspath.domain.model.Rutina
import edu.ucne.atlaspath.domain.repository.RoutineRepository
import javax.inject.Inject

class SaveRutinaUseCase @Inject constructor(
    private val repository: RoutineRepository
) {
    suspend operator fun invoke(rutina: Rutina) = repository.saveRutina(rutina)
}