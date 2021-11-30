package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.Instant
import kotlin.math.max
import kotlin.math.pow
import kotlin.random.Random
import java.lang.Float.max as floatMax

@Serializable
data class PlantType(
    override val displayName: String,
    val chromosome: Chromosome,
    val autoPlantChecked: Boolean = false,
    val id: Int,
    val lineage: Pair<PlantType, PlantType>? = null,
    val isDefault: Boolean = false,
): Describe, Purchasable {
    override val cost = Currency(
        total = chromosome.totalPopCount.toDouble().pow(2.0).toLong()
    )

    companion object {
        val TOTAL_PEPPER_TYPES = 5 + NAMES.totalCombinations
        private const val MIN_GROWTH = 0.25f

        fun SerializableRandom.plantId() = this.nextInt(5, Int.MAX_VALUE)

        private val geneRandom = Random(118) // have this the same across games

        val BellPepper = PlantType(
            displayName = "Bell Pepper",
            chromosome = Chromosome(
                pepperYield = Gene.withOneBits(bitCount = 4, random = geneRandom)
            ),
            id = 1,
            isDefault = true,
        )
        val Poblano = PlantType(
            displayName = "Poblano",
            chromosome = Chromosome(
                scovilleCount = Gene.withOneBits(bitCount = 2, random = geneRandom),
                pepperYield = Gene.withOneBits(bitCount = 3, random = geneRandom),
            ),
            id = 2,
            isDefault = true,
        )
        val Guajillo = PlantType(
            displayName = "Guajillo",
            chromosome = Chromosome(
                scovilleCount = Gene.withOneBits(bitCount = 3, random = geneRandom),
                pepperYield = Gene.withOneBits(bitCount = 2, random = geneRandom),
            ),
            id = 3,
            isDefault = true,
        )
        val Jalapeno = PlantType(
            displayName = "Jalapeño",
            chromosome = Chromosome(
                scovilleCount = Gene.withOneBits(bitCount = 4, random = geneRandom),
                pepperYield = Gene.withOneBits(bitCount = 2, random = geneRandom),
            ),
            id = 4,
            isDefault = true,
        )
        val BirdsEye = PlantType(
            displayName = "Bird's Eye",
            chromosome = Chromosome(
                scovilleCount = Gene.withOneBits(bitCount = 8, random = geneRandom),
                pepperYield = Gene.withOneBits(bitCount = 1, random = geneRandom),
            ),
            id = 5,
            isDefault = true,
        )
    }

    private val growthDuration: Float
        get() {
            val power = chromosome.growthDuration.popCount().toFloat() / Gene.SIZE_BITS
            return floatMax(MIN_GROWTH, 1.0f - power)
        }

    val phases = Phases.ofScale(growthDuration)

    val yield: Long
        get() {
            return chromosome.pepperYield.popCount() * 10L
        }

    val size: Long
        get() {
            return max(1, chromosome.pepperSize.popCount().toLong())
        }

    val scovilles: Scovilles
        get() {
            val popCount = chromosome.scovilleCount.popCount()
            return Scovilles(
                popCount.toDouble()
                    .pow(2)
                    .toLong()
                    .times(1000))
        }

    fun toSeed() = Seed(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlantType

        if (id != other.id) return false

        return true
    }

}

@Serializable
data class PlantPot(val plant: Plant?)

@Serializable
data class Plant(
    val plantType: PlantType,
    val lightStrength: Int, // Less clear than medium, but lets be consistent
    val mediumEffectiveness: Int, // Avoid upgrades cheating the system
    val epochMillis: Long,
) {
    fun currentPhase(currentEpochMillis: Long) =
        plantType.phases.currentPhase(
            Duration.between(
                Instant.ofEpochMilli(epochMillis),
                Instant.ofEpochMilli(currentEpochMillis),
            )
        )

    fun isGrowing(currentEpochMillis: Long): Boolean {
        val phase = currentPhase(currentEpochMillis)
        return !(phase == null || phase.isRipe)
    }

    fun isRipe(currentEpochMillis: Long) =
        currentPhase(currentEpochMillis)?.isRipe ?: false

    fun isDead(currentEpochMillis: Long) = currentPhase(currentEpochMillis) == null

    fun harvest() = plantType.yield * lightStrength * mediumEffectiveness
}

data class Seed(val plantType: PlantType)