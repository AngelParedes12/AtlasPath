package edu.ucne.atlaspath.data.local.mapper

import edu.ucne.atlaspath.data.local.entity.EjercicioEntity
import edu.ucne.atlaspath.data.local.entity.RoutineEntity
import edu.ucne.atlaspath.data.local.entity.RegistroEntity
import edu.ucne.atlaspath.data.local.entity.SessionEntity
import edu.ucne.atlaspath.domain.model.Ejercicio
import edu.ucne.atlaspath.domain.model.Rutina
import edu.ucne.atlaspath.domain.model.RegistroEjercicio
import edu.ucne.atlaspath.domain.model.Sesion

fun RoutineEntity.toDomain() = Rutina(
    rutinaId = rutinaId ?: 0,
    titulo = titulo,
    descripcion = descripcion,
    ejercicios = ejercicios.map { it.toDomain() }
)

fun Rutina.toEntity() = RoutineEntity(
    rutinaId = if (rutinaId == 0) null else rutinaId,
    titulo = titulo,
    descripcion = descripcion,
    ejercicios = ejercicios.map { it.toEntity() }
)

fun EjercicioEntity.toDomain() = Ejercicio(
    nombre = nombre,
    series = series,
    repeticiones = repeticiones,
    descansoSegundos = descansoSegundos,
    grupoMuscular = grupoMuscular ?: "General"
)

fun Ejercicio.toEntity() = EjercicioEntity(
    nombre = nombre,
    series = series,
    repeticiones = repeticiones,
    descansoSegundos = descansoSegundos,
    grupoMuscular = grupoMuscular
)

fun SessionEntity.toDomain() = Sesion(
    sesionId = sesionId ?: 0,
    rutinaId = rutinaId,
    fechaInicio = fechaInicio,
    fechaFin = fechaFin,
    volumenTotalLbs = volumenTotalLbs,
    xpGanada = xpGanada,
    registros = registros.map { it.toDomain() }
)

fun Sesion.toEntity() = SessionEntity(
    sesionId = if (sesionId == 0) null else sesionId,
    rutinaId = rutinaId,
    fechaInicio = fechaInicio,
    fechaFin = fechaFin,
    volumenTotalLbs = volumenTotalLbs,
    xpGanada = xpGanada,
    registros = registros.map { it.toEntity() }
)

fun RegistroEntity.toDomain() = RegistroEjercicio(
    ejercicioNombre = ejercicioNombre,
    grupoMuscular = grupoMuscular,
    pesoLbs = pesoLbs,
    repeticionesHechas = repeticionesHechas
)

fun RegistroEjercicio.toEntity() = RegistroEntity(
    ejercicioNombre = ejercicioNombre,
    grupoMuscular = grupoMuscular,
    pesoLbs = pesoLbs,
    repeticionesHechas = repeticionesHechas
)