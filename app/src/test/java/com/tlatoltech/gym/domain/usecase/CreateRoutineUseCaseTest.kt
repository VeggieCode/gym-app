package com.tlatoltech.gym.domain.usecase

import com.tlatoltech.gym.domain.model.Routine
import com.tlatoltech.gym.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

// Nuestro Repositorio Falso
class FakeRoutineRepository : RoutineRepository {
    var wasRoutineSaved = false

    override fun observeActiveRoutines(): Flow<List<Routine>> = emptyFlow()

    override suspend fun saveRoutine(routine: Routine): Routine {
        wasRoutineSaved = true
        return routine.copy(id = 999)
    }

    override suspend fun syncPendingRoutines() {
        TODO("Not yet implemented")
    }
}

class CreateRoutineUseCaseTest {

    @Test
    fun `Si los datos son validos, llama al repositorio y retorna Exito`() = runTest {
        val fakeRepo = FakeRoutineRepository()
        val useCase = CreateRoutineUseCase(fakeRepo)

        val ejerciciosCrudos = listOf(Triple("Press", 4, 10))

        val result = useCase.invoke("Fuerza", listOf("Lunes"), ejerciciosCrudos)

        assertTrue("El resultado debería ser Success", result.isSuccess)
        assertTrue("El repositorio DEBIÓ ser llamado", fakeRepo.wasRoutineSaved)
        assertEquals(999, result.getOrNull()?.id)
    }

    @Test
    fun `Si el ejercicio es invalido, retorna Falla y NO toca el repositorio`() = runTest {
        val fakeRepo = FakeRoutineRepository()
        val useCase = CreateRoutineUseCase(fakeRepo)

        // Dato inválido: -5 series
        val ejerciciosCrudos = listOf(Triple("Press", -5, 10))

        val result = useCase.invoke("Fuerza", listOf("Lunes"), ejerciciosCrudos)

        assertTrue("El resultado debería ser Failure", result.isFailure)
        assertFalse("El repositorio NUNCA debió llamarse", fakeRepo.wasRoutineSaved)
    }
}