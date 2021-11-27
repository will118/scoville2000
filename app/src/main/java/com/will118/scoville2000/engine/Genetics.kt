package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*
import kotlin.math.roundToInt
import kotlin.random.Random

@Serializable
enum class GeneticTrait(override val displayName: String) : Describe {
    PepperYield("Yield"),
    PepperSize("Size"),
    ScovilleCount("Spice"),
    GrowthDuration("Speed"),
}

@Serializable
data class Gene(
    val lsb: ULong,
    val msb: ULong,
) {
    fun popCount() = lsb.countOneBits() + msb.countOneBits()

    fun cross(other: Gene, crossover: Int): Gene {
        val crossoverMask = (0UL).inv().shr(crossover)
        val crossoverMaskInv = crossoverMask.inv()

        val retainedLsb = lsb.and(crossoverMask)
        val retainedMsb = msb.and(crossoverMask)

        return Gene(
            lsb = other.lsb.and(crossoverMaskInv).or(retainedLsb),
            msb = other.msb.and(crossoverMaskInv).or(retainedMsb),
        )
    }
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

    fun cross(right: Chromosome, crossover: Int): Chromosome {
        return Chromosome(
            growthDuration = growthDuration.cross(right.growthDuration, crossover = crossover),
            scovilleCount = scovilleCount.cross(right.scovilleCount, crossover = crossover),
            pepperSize = pepperSize.cross(right.pepperSize, crossover = crossover),
            pepperYield = pepperYield.cross(pepperYield, crossover = crossover),
        )
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

        val sumUnchanged = GeneticTrait.values().fold(0.0f) { acc, t ->
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
    private val randomSeed: Int = Random.nextInt(),
) {
    companion object {
        const val REQUIRED_FITNESS_IMPROVEMENT_PERCENTAGE = 10.0f
        const val POPULATION_SIZE = 25
    }

    @Transient
    private val random = Random(randomSeed).also {
        // Seek the seed
        for (i in 0 until generation) {
            it.nextInt()
        }
    }

    private fun Random.nextCrossover() = this.nextInt(0, 65)

    @Transient
    val population = PriorityQueue(POPULATION_SIZE, compareBy<PlantType>(
        { it.chromosome.fitness(fitnessFunctionData) },
        { it.id.lsb },
        { it.id.msb },
    )).also { it.addAll(serializedPopulation) }

    init {
        if (population.size < POPULATION_SIZE) {
            // use a new random because we only do this once
            val r = Random.Default

            for (i in population.size until POPULATION_SIZE) {
                population.add(
                    cross(
                        left = leftPlantType,
                        right = rightPlantType,
                        crossover = r.nextCrossover(),
                    )
                )
            }
        }
    }

    private fun cross(left: PlantType, right: PlantType, crossover: Int) = left.copy(
        chromosome = left.chromosome.cross(right.chromosome, crossover = crossover),
        id = ObjectId.random(),
    )

    private fun PlantType.maybeMutate(): PlantType {
        if (random.nextInt() % 7 == 0) {
            val mutationPoint = random.nextCrossover()
            when (mutationPoint % 4) {
                0 -> this.chromosome.pepperYield
                1 -> this.chromosome.pepperSize
                2 -> this.chromosome.scovilleCount
                3 -> this.chromosome.growthDuration
            }
            return this.
        }
        return this
    }

    fun tickGenerations(n: Int): GeneticComputationState {
        var newGeneration = generation

        for (i in 0 until n) {
            newGeneration++

            // we should be able to remove things from the pq without the UI updating
            val fittest = population.poll()!!
            val secondFittest = population.poll()!!

            val crossover = random.nextCrossover()

            val crossA = cross(fittest, secondFittest, crossover).maybeMutate()
            val crossB = cross(secondFittest, fittest, crossover).maybeMutate()

            population.apply {
                add(fittest)
                add(secondFittest)
                add(crossA)
                add(crossB)
            }
        }

        return this.copy(
            generation = newGeneration,
        )
    }

    fun progress(): Int {
        val topFitnessValue = population.peek()!!.chromosome.fitness(fitnessFunctionData)
        val range = targetFitnessValue - originalFitnessValue
        val progress = (topFitnessValue / range) * 100
        return progress.roundToInt()
    }

    private val originalFitnessValue = leftPlantType.chromosome.fitness(fitnessFunctionData)
        .plus(rightPlantType.chromosome.fitness(fitnessFunctionData))
        .div(2)

    private val targetFitnessValue = originalFitnessValue
        .plus(originalFitnessValue / REQUIRED_FITNESS_IMPROVEMENT_PERCENTAGE)
}