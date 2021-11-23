package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

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
    return (0UL).inv().shr(64 - n)
}

private val emptyGene = Gene(lsb = 0UL, msb = 0UL)

@Serializable
data class Chromosome(
    val pepperYield: Gene = emptyGene,
    val scovilleCount: Gene = emptyGene,
    val pepperSize: Gene = emptyGene,
    val growthDuration: Gene = emptyGene,
) {
    fun fitness(goals: Map<GeneticTrait, Float>): Float {
        return goals.entries.fold(0f) { acc, entry ->
            acc + when (entry.key) {
                GeneticTrait.PepperYield -> pepperYield
                GeneticTrait.ScovilleCount -> scovilleCount
                GeneticTrait.PepperSize -> pepperSize
                GeneticTrait.GrowthDuration -> growthDuration
            }.popCount().times(entry.value)
        }
    }
}

@Serializable
data class FitnessFunction(
    val pepperYield: Int = 250,
    val scovilleCount: Int = 250,
    val pepperSize: Int = 250,
    val growthDuration: Int = 250,
) {
    companion object {
        const val MAX = 1000
        const val VALUES = 4
        val TRAITS = listOf(
            GeneticTrait.PepperYield,
            GeneticTrait.ScovilleCount,
            GeneticTrait.PepperSize,
            GeneticTrait.GrowthDuration,
        )
    }

    fun getValue(trait: GeneticTrait): Int {
        return when (trait) {
            GeneticTrait.PepperYield -> pepperYield
            GeneticTrait.ScovilleCount -> scovilleCount
            GeneticTrait.PepperSize -> pepperSize
            GeneticTrait.GrowthDuration -> growthDuration
        }
    }

    fun setValue(trait: GeneticTrait, newValue: Float): FitnessFunction {
        val newNormalizedValue = (newValue * 1000).roundToInt()

        var newTotal = TRAITS.fold(0) { acc, t ->
            acc + when (t) {
                trait -> newNormalizedValue
                GeneticTrait.PepperYield -> pepperYield
                GeneticTrait.PepperSize -> pepperSize
                GeneticTrait.ScovilleCount -> scovilleCount
                GeneticTrait.GrowthDuration -> growthDuration
            }
        }

        var newPepperYield = pepperYield
        var newScovilleCount = scovilleCount
        var newGrowthDuration = growthDuration
        var newPepperSize = pepperSize

        loop@while (newTotal != MAX) {
            val isIncrement = newTotal > MAX
            val amount = if (isIncrement) 1 else -1

            for (t in TRAITS) {
                val isTarget = t == trait
                if (newTotal == MAX && !isTarget) break@loop

                when (t) {
                    GeneticTrait.PepperYield -> {
                        if (newPepperYield == 0 && isIncrement && !isTarget) continue
                        newPepperYield =
                            if (isTarget)
                                newNormalizedValue
                            else {
                                newTotal -= amount
                                newPepperYield - amount
                            }
                    }
                    GeneticTrait.ScovilleCount -> {
                        if (newScovilleCount == 0 && isIncrement && !isTarget) continue
                        newScovilleCount =
                            if (isTarget)
                                newNormalizedValue
                            else {
                                newTotal -= amount
                                newScovilleCount - amount
                            }
                }
                    GeneticTrait.PepperSize -> {
                        if (newPepperSize == 0 && isIncrement && !isTarget) continue
                        newPepperSize =
                            if (isTarget)
                                newNormalizedValue
                            else {
                                newTotal -= amount
                                newPepperSize - amount
                            }
                    }
                    GeneticTrait.GrowthDuration -> {
                        if (newGrowthDuration == 0 && isIncrement && !isTarget) continue
                        newGrowthDuration =
                            if (isTarget)
                                newNormalizedValue
                            else {
                                newTotal -= amount
                                newGrowthDuration - amount
                            }
                    }
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
    val population: List<PlantType>,
    val fitnessFunction: FitnessFunction,
)