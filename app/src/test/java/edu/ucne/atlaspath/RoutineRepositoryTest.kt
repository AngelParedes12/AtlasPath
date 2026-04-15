package edu.ucne.atlaspath

import edu.ucne.atlaspath.data.local.dao.ExerciseDao
import edu.ucne.atlaspath.data.local.dao.RoutineDao
import edu.ucne.atlaspath.data.local.dao.SessionDao
import edu.ucne.atlaspath.data.local.entity.RoutineEntity
import edu.ucne.atlaspath.data.remote.Resource
import edu.ucne.atlaspath.data.repository.RoutineRepositoryImpl
import edu.ucne.atlaspath.domain.model.Rutina
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RoutineRepositoryTest {

    private lateinit var repository: RoutineRepositoryImpl
    private val mockRoutineDao = mockk<RoutineDao>(relaxed = true)
    private val mockSessionDao = mockk<SessionDao>(relaxed = true)
    private val mockExerciseDao = mockk<ExerciseDao>(relaxed = true)

    @Before
    fun setup() {
        repository = RoutineRepositoryImpl(
            routineDao = mockRoutineDao,
            sessionDao = mockSessionDao,
            exerciseDao = mockExerciseDao
        )
    }

    @Test
    fun `getRoutines should emit list of Rutina from local DAO`() = runTest {
        val mockEntities = listOf(
            RoutineEntity(rutinaId = 1, titulo = "Rutina A", descripcion = "Desc A", ejercicios = emptyList()),
            RoutineEntity(rutinaId = 2, titulo = "Rutina B", descripcion = "Desc B", ejercicios = emptyList())
        )

        coEvery { mockRoutineDao.getAll(any()) } returns flowOf(mockEntities)

        val resultFlow = repository.getRoutines()
        val resultList = resultFlow.first()

        assertTrue(resultList is Resource.Success<*>)

        val successResult = resultList as Resource.Success<List<Rutina>>
        assertEquals(2, successResult.data?.size)
        assertEquals("Rutina A", successResult.data?.get(0)?.titulo)

        coVerify(exactly = 1) { mockRoutineDao.getAll(any()) }
    }

    @Test
    fun `saveRutina should call DAO save and return Success`() = runTest {
        val rutina = Rutina(rutinaId = 0, titulo = "Nueva Rutina", descripcion = "Test", ejercicios = emptyList())

        coEvery { mockRoutineDao.save(any()) } returns Unit

        val result = repository.saveRutina(rutina)

        assertTrue(result is Resource.Success<*>)
        coVerify(exactly = 1) { mockRoutineDao.save(any()) }
    }

    @Test
    fun `deleteRutina should call DAO deleteById`() = runTest {
        val idAEliminar = 1

        coEvery { mockRoutineDao.deleteById(idAEliminar) } returns Unit

        repository.deleteRutina(idAEliminar)

        coVerify(exactly = 1) { mockRoutineDao.deleteById(idAEliminar) }
    }
}