package com.will118.scoville2000.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeneticStateTests {
    private companion object {
        const val SEED = 12345
    }

    @Test
    fun `generates population`() {
        var state = GeneticComputationState(
            leftPlantType = PlantType.BellPepper,
            rightPlantType = PlantType.BirdsEye,
            isActive = false,
            fitnessFunctionData = FitnessFunctionData(),
            generation = 0,
            serializedPopulation = emptyList(),
            random = SerializableRandom.fromSeed(SEED),
        )

        assertEquals(GeneticComputationState.POPULATION_SIZE, state.population.size)
    }

    @Test
    fun `generations increase progression`() {
        var state = GeneticComputationState(
            leftPlantType = PlantType.BellPepper,
            rightPlantType = PlantType.BirdsEye,
            isActive = false,
            fitnessFunctionData = FitnessFunctionData(),
            generation = 0,
            serializedPopulation = emptyList(),
            random = SerializableRandom.fromSeed(SEED),
        )

        // tickGenerations mutates the population so we have to get progress here
        val originalProgress = state.progress()

        state = state.tickGenerations(n = 20)

        assertTrue(state.progress() > originalProgress)
    }

    @Test
    fun `sample progress`() {
        // this test is to help tune the genetics
        var state = GeneticComputationState(
            leftPlantType = PlantType.BellPepper,
            rightPlantType = PlantType.BirdsEye,
            isActive = false,
            fitnessFunctionData = FitnessFunctionData(),
            generation = 0,
            serializedPopulation = emptyList(),
            random = SerializableRandom.fromSeed(SEED),
        )

        assertEquals(0, state.progress())

        state = state.tickGenerations(50)

        assertEquals(49, state.progress())

        state = state.tickGenerations(50)

        assertEquals(83, state.progress())

        state = state.tickGenerations(50)

        assertEquals(100, state.progress())
    }
}