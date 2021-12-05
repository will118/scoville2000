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
    val visible: Boolean = false,
    val id: Int,
    val lineage: Pair<PlantType, PlantType>? = null,
): Describe, Purchasable {
    companion object {
        private const val MIN_GROWTH = 0.25f

        private val geneRandom = Random(118) // have this the same across games

        val BellPepper = PlantType(
            displayName = "Bell Pepper",
            chromosome = Chromosome(
                pepperYield = Gene.withOneBits(bitCount = 10, random = geneRandom),
                pepperSize = Gene.withOneBits(bitCount = 40),
            ),
            id = 1,
        )
        val Poblano = PlantType(
            displayName = "Poblano",
            chromosome = Chromosome(
                scovilleCount = Gene.withOneBits(bitCount = 2, random = geneRandom),
                pepperYield = Gene.withOneBits(bitCount = 15, random = geneRandom),
                pepperSize = Gene.withOneBits(bitCount = 10),
            ),
            id = 2,
        )
        val Guajillo = PlantType(
            displayName = "Guajillo",
            chromosome = Chromosome(
                scovilleCount = Gene.withOneBits(bitCount = 3, random = geneRandom),
                pepperYield = Gene.withOneBits(bitCount = 10, random = geneRandom),
                pepperSize = Gene.withOneBits(bitCount = 5),
            ),
            id = 3,
        )
        val Jalapeno = PlantType(
            displayName = "Jalapeño",
            chromosome = Chromosome(
                scovilleCount = Gene.withOneBits(bitCount = 4, random = geneRandom),
                pepperYield = Gene.withOneBits(bitCount = 10, random = geneRandom),
                pepperSize = Gene.withOneBits(bitCount = 4),
            ),
            id = 4,
        )
        val BirdsEye = PlantType(
            displayName = "Bird's Eye",
            chromosome = Chromosome(
                scovilleCount = Gene.withOneBits(bitCount = 8, random = geneRandom),
                pepperYield = Gene.withOneBits(bitCount = 5, random = geneRandom),
                pepperSize = Gene.withOneBits(bitCount = 1),
            ),
            id = 5,
        )

        object NAMES {
            // Shouldn't really be about how spicy
            val adjectives = listOf(
                "Spotted", // 0
                "Warm", // 1
                "Spicy", // 2
                "Enduring", // 3
                "Fearsome", // 4
                "Volcanic", // 5
                "Ancient", // 6
                "Infernal", // 7
            )

            // More about how spicy the pepper is
            val peppers = listOf(
                "Pepper", // 0
                "Wiggler", // 1
                "Tickler", // 2
                "Scorcher", // 3
                "Fireball", // 4
                "Dragon", // 5
            )

            val totalCombinations = adjectives.size * peppers.size
        }

        fun allPlants(): List<PlantType> {
            return listOf(
                BellPepper,
                Poblano,
                Guajillo,
                Jalapeno,
                BirdsEye,
            ) + NAMES.peppers.flatMapIndexed { i, pepper ->
                NAMES.adjectives.mapIndexed { j, adj ->
                    PlantType(
                        displayName = "$adj $pepper",
                        chromosome = Chromosome(),
                        id = 6 + ((i * NAMES.adjectives.size) + j),
                    )
                }
            }
        }
    }

    override val cost = Currency(total = chromosome.totalPopCount.toLong())

    private val growthDuration: Float
        get() {
            val power = chromosome.growthDuration.popCount().toFloat() / Gene.SIZE_BITS
            return floatMax(MIN_GROWTH, 1.0f - power)
        }

    val phases = Phases.ofScale(growthDuration)

    val yield: Long
        get() {
            return chromosome.pepperYield.popCount() * 2L
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