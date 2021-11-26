package com.will118.scoville2000.engine

import org.junit.Test

class GeneticStateTests {
    @Test
    fun `eventually completes`() {
        var state = GeneticComputationState(
            leftPlantType = PlantType.BellPepper,
            rightPlantType = PlantType.BirdsEye,
            isActive = false,
            fitnessFunctionData = FitnessFunctionData(),
            generation = 0,
            serializedPopulation = emptyList(),
        )

        assertEqual(state.population.size, GeneticComputationState.POPULATION_SIZE)
    }
}