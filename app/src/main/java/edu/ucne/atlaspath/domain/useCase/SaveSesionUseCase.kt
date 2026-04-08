package edu.ucne.atlaspath.domain.useCase

import edu.ucne.atlaspath.domain.model.sesion
import edu.ucne.atlaspath.domain.repository.RoutineRepository // Asumiendo que extendemos el repo o creamos SessionRepository
import javax.inject.Inject

class SaveSesionUseCase @Inject constructor(
    private val repository: RoutineRepository
) {
    suspend operator fun invoke(sesion: sesion) = repository.saveSesion(sesion)
}