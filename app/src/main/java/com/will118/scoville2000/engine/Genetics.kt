package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
enum class GeneticTrait(override val displayName: String) : Describe {
    PepperYield("Yield"),
    PepperSize("Size"),
    ScovilleCount("Spice"),
    GrowthDuration("Speed"),
}

@Serializable
data class Gene(
    private val lsb: ULong,
    private val msb: ULong,
) {
    fun popCount() = lsb.countOneBits() + msb.countOneBits()
}

fun flipBits(n: Int): ULong {
    if (n == 0) return 0UL
    return (0UL).inv().shr(ULong.SIZE_BITS - n)
}

private val emptyGene = Gene(lsb = 0UL, msb = 0UL)

@Serializable
data class Chromosome(
    val pepperYield: Gene = emptyGene,
    val scovilleCount: Gene = emptyGene,
    val pepperSize: Gene = emptyGene,
    val growthDuration: Gene = emptyGene,
) {
    fun fitness(fitnessFunction: FitnessFunctionData): Float {
        return GeneticTrait.values().fold(0f) { acc, trait ->
            acc + when (trait) {
                GeneticTrait.PepperYield ->
                    pepperYield.popCount().times(fitnessFunction.pepperYield)
                GeneticTrait.ScovilleCount ->
                    scovilleCount.popCount().times(fitnessFunction.scovilleCount)
                GeneticTrait.PepperSize ->
                    pepperSize.popCount().times(fitnessFunction.pepperSize)
                GeneticTrait.GrowthDuration ->
                    growthDuration.popCount().times(fitnessFunction.growthDuration)
            }
        }
    }
}

@Serializable
data class FitnessFunctionData(
    val pepperYield: Float = 0.25f,
    val scovilleCount: Float = 0.25f,
    val pepperSize: Float = 0.25f,
    val growthDuration: Float = 0.25f,
) {
    companion object {
        const val MAX = 1.0f
        const val VALUES = 4
    }

    fun getValue(trait: GeneticTrait): Float {
        return when (trait) {
            GeneticTrait.PepperYield -> pepperYield
            GeneticTrait.ScovilleCount -> scovilleCount
            GeneticTrait.PepperSize -> pepperSize
            GeneticTrait.GrowthDuration -> growthDuration
        }
    }

    fun setValue(trait: GeneticTrait, newValue: Float): FitnessFunctionData {
        val unchangedNewPortion = 1.0f - newValue

        var sumUnchanged = GeneticTrait.values().fold(0.0f) { acc, t ->
            when (t) {
                trait -> acc
                GeneticTrait.PepperYield -> acc + pepperYield
                GeneticTrait.PepperSize -> acc + pepperSize
                GeneticTrait.ScovilleCount -> acc + scovilleCount
                GeneticTrait.GrowthDuration -> acc + growthDuration
            }
        }

        var newPepperYield = pepperYield
        var newScovilleCount = scovilleCount
        var newGrowthDuration = growthDuration
        var newPepperSize = pepperSize

        val f: (Float) -> Float = {
            when (sumUnchanged) {
                0.0f -> 1.0f / (VALUES - 1)
                else -> it / sumUnchanged
            } * unchangedNewPortion
        }

        for (t in GeneticTrait.values()) {
            val isTarget = t == trait

            when (t) {
                GeneticTrait.PepperYield -> {
                    newPepperYield = if (isTarget) newValue else f(newPepperYield)
                }
                GeneticTrait.ScovilleCount -> {
                    newScovilleCount = if (isTarget) newValue else f(newScovilleCount)
            }
                GeneticTrait.PepperSize -> {
                    newPepperSize = if (isTarget) newValue else f(newPepperSize)
                }
                GeneticTrait.GrowthDuration -> {
                    newGrowthDuration = if (isTarget) newValue else f(newGrowthDuration)
                }
            }
        }

        return this.copy(
            pepperYield = newPepperYield,
            scovilleCount = newScovilleCount,
            pepperSize = newPepperSize,
            growthDuration = newGrowthDuration,
        )
    }
}

@Serializable
data class GeneticComputationState(
    val leftPlantType: PlantType,
    val rightPlantType: PlantType,
    val isActive: Boolean,
    val generation: Int,
    val fitnessFunctionData: FitnessFunctionData,
    private val serializedPopulation: List<PlantType>,
) {
    private companion object {
        const val REQUIRED_FITNESS_IMPROVEMENT_PERCENTAGE = 10.0f
    }

    @Transient
    val population = serializedPopulation.toSortedSet(compareBy { it.chromosome.fitness(fitnessFunctionData) })

    fun tickGenerations(n: Int): GeneticComputationState {
        var newGeneration = generation

        for (i in 0 until n) {
            newGeneration++

            // selection
            val top2 = population.take(2)
        }

        return this.copy(
            generation = newGeneration,
        )
    }

    private val originalFitnessValue = leftPlantType.chromosome.fitness(fitnessFunctionData)
        .plus(rightPlantType.chromosome.fitness(fitnessFunctionData))
        .div(2)

    private val targetFitnessValue = originalFitnessValue
        .plus(originalFitnessValue / REQUIRED_FITNESS_IMPROVEMENT_PERCENTAGE)
}