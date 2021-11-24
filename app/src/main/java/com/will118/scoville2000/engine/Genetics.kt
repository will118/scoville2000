package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable

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
    val pepperYield: Float = 0.25f,
    val scovilleCount: Float = 0.25f,
    val pepperSize: Float = 0.25f,
    val growthDuration: Float = 0.25f,
) {
    companion object {
        const val MAX = 1.0f
        const val VALUES = 4
        val TRAITS = listOf(
            GeneticTrait.PepperYield,
            GeneticTrait.ScovilleCount,
            GeneticTrait.PepperSize,
            GeneticTrait.GrowthDuration,
        )
    }

    fun getValue(trait: GeneticTrait): Float {
        return when (trait) {
            GeneticTrait.PepperYield -> pepperYield
            GeneticTrait.ScovilleCount -> scovilleCount
            GeneticTrait.PepperSize -> pepperSize
            GeneticTrait.GrowthDuration -> growthDuration
        }
    }

    fun setValue(trait: GeneticTrait, newValue: Float): FitnessFunction {
        val unchangedNewPortion = 1.0f - newValue

        var sumUnchanged = TRAITS.fold(0.0f) { acc, t ->
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

        for (t in TRAITS) {
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
    val population: List<PlantType>,
    val fitnessFunction: FitnessFunction,
)