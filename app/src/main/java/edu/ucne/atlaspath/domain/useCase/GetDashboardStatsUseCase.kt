package edu.ucne.atlaspath.domain.useCase

import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.domain.model.Dashboard
import edu.ucne.atlaspath.domain.model.RangoMuscular
import edu.ucne.atlaspath.domain.model.Sesion
import edu.ucne.atlaspath.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDashboardStatsUseCase @Inject constructor(
    private val repository: RoutineRepository
) {
    operator fun invoke(): Flow<Resource<Dashboard>> {
        return repository.getAllSessions().map { resource ->
            when (resource) {
                is Resource.Success -> {
                    val sesiones = resource.data ?: emptyList()
                    Resource.Success(calcularEstadisticas(sesiones))
                }
                is Resource.Error -> Resource.Error(resource.message ?: "Error al calcular stats")
                is Resource.Loading -> Resource.Loading()
            }
        }
    }

    private fun calcularEstadisticas(sesiones: List<Sesion>): Dashboard{
        var volumenTotal = 0.0
        var xpPorAsistencia = 0

        val maxFuerzaPorMusculo = mutableMapOf<String, Double>()

        sesiones.forEach { sesion ->
            volumenTotal += sesion.volumenTotalLbs
            xpPorAsistencia += 500

            sesion.registros.forEach { registro ->
                val fuerzaEstimada = registro.pesoLbs * (1.0 + (registro.repeticionesHechas / 30.0))
                val musculo = registro.grupoMuscular

                val fuerzaActual = maxFuerzaPorMusculo.getOrDefault(musculo, 0.0)
                if (fuerzaEstimada > fuerzaActual) {
                    maxFuerzaPorMusculo[musculo] = fuerzaEstimada
                }
            }
        }

        val xpTotal = xpPorAsistencia + (volumenTotal * 0.05).toInt()
        val nivel = (xpTotal / 1000) + 1
        val progresoGlobal = (xpTotal % 1000) / 1000f

        val rangos = maxFuerzaPorMusculo.map { (musculo, fuerzaMax) ->
            calcularRangoPorFuerza(musculo, fuerzaMax)
        }.sortedByDescending { it.pesoMaximoLbs }

        return Dashboard(
            totalEntrenamientos = sesiones.size,
            volumenTotalLbs = volumenTotal,
            rachaDias = sesiones.size,
            nivelActual = nivel,
            progresoNivel = progresoGlobal,
            rangosMusculares = rangos
        )
    }

    private fun calcularRangoPorFuerza(musculo: String, fuerzaMaxLbs: Double): RangoMuscular {
        val multiplicadorDificultad = when (musculo.lowercase()) {
            "piernas" -> 1.5
            "espalda" -> 1.2
            "pecho" -> 1.0
            "hombros", "brazos" -> 0.6
            else -> 1.0
        }

        val limiteBronce = 90.0 * multiplicadorDificultad
        val limitePlata = 150.0 * multiplicadorDificultad
        val limiteOro = 225.0 * multiplicadorDificultad
        val limitePlatino = 315.0 * multiplicadorDificultad

        return when {
            fuerzaMaxLbs < limiteBronce -> RangoMuscular(
                musculo, "Hierro", (fuerzaMaxLbs / limiteBronce).toFloat(), "🧱", fuerzaMaxLbs
            )
            fuerzaMaxLbs < limitePlata -> RangoMuscular(
                musculo, "Bronce", ((fuerzaMaxLbs - limiteBronce) / (limitePlata - limiteBronce)).toFloat(), "🥉", fuerzaMaxLbs
            )
            fuerzaMaxLbs < limiteOro -> RangoMuscular(
                musculo, "Plata", ((fuerzaMaxLbs - limitePlata) / (limiteOro - limitePlata)).toFloat(), "🥈", fuerzaMaxLbs
            )
            fuerzaMaxLbs < limitePlatino -> RangoMuscular(
                musculo, "Oro", ((fuerzaMaxLbs - limiteOro) / (limitePlatino - limiteOro)).toFloat(), "🥇", fuerzaMaxLbs
            )
            else -> RangoMuscular(
                musculo, "Platino", 1f, "💎", fuerzaMaxLbs
            )
        }
    }
}