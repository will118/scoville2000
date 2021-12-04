package com.will118.scoville2000.engine

import com.will118.scoville2000.engine.PlantType.Companion.plantId
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*
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
    val lo: ULong,
    val hi: ULong,
) {
    companion object {
        const val SIZE_BITS = ULong.SIZE_BITS + ULong.SIZE_BITS

        fun withOneBits(bitCount: Int, random: Random = Random.Default) =
            sequence { for (i in 0 until 64) { yield(true to i); yield(false to i) } }
                .shuffled(random)
                .distinct()
                .take(bitCount)
                .fold(Gene(lo = 0UL, hi = 0UL)) { acc, indexedValue ->
                    val num = indexedValue.second
                    if (indexedValue.first)
                        acc.copy(lo = acc.lo.or((1UL).shl(num)) )
                    else
                        acc.copy(hi = acc.hi.or((1UL).shl(num)) )
                }
    }

    fun popCount() = lo.countOneBits() + hi.countOneBits()

    fun mutate(bitIndex: Int): Gene {
        val toggleMask = (1UL).shl(bitIndex)

        return Gene(
            lo = lo.xor(toggleMask),
            hi = hi.xor(toggleMask),
        )
    }

    fun cross(other: Gene, crossover: Int): Gene {
        require(crossover < ULong.SIZE_BITS)
        require(crossover >= 0)

        val crossoverMask = (0UL).inv().shl(crossover)
        val crossoverMaskInv = crossoverMask.inv()

        val retainedLsb = lo.and(crossoverMask)
        val retainedMsb = hi.and(crossoverMask)

        return Gene(
            lo = other.lo.and(crossoverMaskInv).or(retainedLsb),
            hi = other.hi.and(crossoverMaskInv).or(retainedMsb),
        )
    }
}

private val emptyGene = Gene(lo = 0UL, hi = 0UL)

@Serializable
data class Chromosome(
    val pepperYield: Gene = emptyGene,
    val scovilleCount: Gene = emptyGene,
    val pepperSize: Gene = emptyGene,
    val growthDuration: Gene = emptyGene,
) {
    companion object {
        const val TOTAL_BITS = Gene.SIZE_BITS * 4
    }

    val totalPopCount = pepperSize.popCount()
        .plus(pepperYield.popCount())
        .plus(scovilleCount.popCount())
        .plus(growthDuration.popCount())

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

    fun mutate(mutationPoint: Int) = when (mutationPoint % 4) {
        0 -> copy(pepperYield = pepperYield.mutate(mutationPoint))
        1 -> copy(pepperSize = pepperSize.mutate(mutationPoint))
        2 -> copy(scovilleCount = scovilleCount.mutate(mutationPoint))
        3 -> copy(growthDuration = growthDuration.mutate(mutationPoint))
        else -> throw Exception("unreachable")
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
    val maxGeneration: Int,
    val fitnessFunctionData: FitnessFunctionData,
    val wasStarted: Boolean = false,
    private val serializedPopulation: List<PlantType>,
    private var random: SerializableRandom = SerializableRandom.fromSeed(),
) {
    companion object {
        const val POPULATION_SIZE = 25

        fun default() = GeneticComputationState(
            leftPlantType = PlantType.BellPepper,
            rightPlantType = PlantType.BellPepper,
            isActive = false,
            fitnessFunctionData = FitnessFunctionData(),
            generation = 0,
            maxGeneration = 50,
            serializedPopulation = emptyList(),
        )
        private fun SerializableRandom.nextGeneIndex() = this.nextInt(0, 64) // 0..63
    }

    @Transient
    val population = TreeSet(compareBy<PlantType>(
        { it.chromosome.fitness(fitnessFunctionData) },
        { it.id }
    )).also { it.addAll(serializedPopulation) }

    init {
        repeat(POPULATION_SIZE - population.size) {
            population.add(
                cross(leftPlantType, rightPlantType, random.nextGeneIndex())
            )
        }
    }

    // Used to clean up the least fit (n.b. it will be the argument if that is the least fit)
    private fun swapIntoPopulation(plantType: PlantType) {
        population.add(plantType)
        if (population.size > POPULATION_SIZE) {
            population.pollFirst() // the lowest
        }
    }

    private fun cross(left: PlantType, right: PlantType, crossover: Int) = left.copy(
        displayName = "",
        chromosome = left.chromosome.cross(right.chromosome, crossover = crossover),
        id = random.plantId(),
    )

    private fun PlantType.crossAndMaybeMutate(
        other: PlantType,
        crossover: Int,
    ): PlantType {
        val crossed = cross(this, other, crossover)

        if (random.nextInt() % 2 == 0) {
            val mutationPoint = random.nextGeneIndex()
            val c = crossed.chromosome.mutate(mutationPoint)
            return crossed.copy(chromosome = c)
        }

        return crossed
    }

    fun tickGenerations(n: Int): GeneticComputationState {
        for (i in 0 until n) {
            // we should be able to remove things from the pq without the UI updating
            val fittest = population.pollLast()
            val secondFittest = population.pollLast()

            val crossover = random.nextGeneIndex()

            val crossA = fittest.crossAndMaybeMutate(secondFittest, crossover)
            val crossB = secondFittest.crossAndMaybeMutate(fittest, crossover)

            swapIntoPopulation(fittest)
            swapIntoPopulation(secondFittest)
            swapIntoPopulation(crossA)
            swapIntoPopulation(crossB)
        }

        return this.copy(
            generation = generation + n,
            serializedPopulation = population.toList(),
        )
    }

    // State library is quite annoying about hashcodes changing.
    // Let's hope we know best...
    override fun hashCode(): Int = generation

    fun progress() = (generation.toFloat() / maxGeneration) * 100

    fun final(): PlantType {
        val fittest = population.last()
        val name = nameCross(fittest.chromosome)
        return fittest.copy(
            lineage = Pair(leftPlantType, rightPlantType),
            displayName = name,
            id = random.plantId(),
            autoPlantChecked = false,
            isDefault = false,
        )
    }

    fun snapshot(): GeneticComputationState {
        return this.copy(serializedPopulation = population.toList())
    }
}