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
            randomSeed = SEED,
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
            randomSeed = SEED,
        )

        val newState = state.tickGenerations(20)

        assertTrue(newState.progress() > state.progress())
    }
}