package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.Instant
import kotlin.math.pow
import kotlin.random.Random

@Serializable
data class PlantType(
    override val displayName: String,
    val phases: Phases,
    val chromosome: Chromosome,
    val autoPlantChecked: Boolean = false,
    val id: Int,
): Describe, Purchasable {
    override val cost = Currency(
        total = chromosome.totalPopCount.toDouble().pow(2.0).toLong()
    )

    companion object {
        // TODO: would be nice to derive from code
        const val TOTAL_PEPPER_TYPES = 99

        fun SerializableRandom.plantId() = this.nextInt(5, Int.MAX_VALUE)

        private val geneRandom = Random(118) // have this the same across games

        val BellPepper = PlantType(
            displayName = "Bell Pepper",
            phases = Phases.DEFAULT,
            chromosome = Chromosome(
                pepperYield = Gene.withOneBits(bitCount = 2, random = geneRandom)
            ),
            id = 1,
        )
        val Poblano = PlantType(
            displayName = "Poblano",
            chromosome = Chromosome(
                scovilleCount = Gene.withOneBits(bitCount = 2, random = geneRandom),
                pepperYield = Gene.withOneBits(bitCount = 3, random = geneRandom),
            ),
            phases = Phases.DEFAULT,
            id = 2,
        )
        val Guajillo = PlantType(
            displayName = "Guajillo",
            chromosome = Chromosome(
                scovilleCount = Gene.withOneBits(bitCount = 3, random = geneRandom),
                pepperYield = Gene.withOneBits(bitCount = 2, random = geneRandom),
            ),
            phases = Phases.DEFAULT,
            id = 3,
        )
        val Jalapeno = PlantType(
            displayName = "Jalapeño",
            chromosome = Chromosome(
                scovilleCount = Gene.withOneBits(bitCount = 4, random = geneRandom),
                pepperYield = Gene.withOneBits(bitCount = 2, random = geneRandom),
            ),
            phases = Phases.DEFAULT,
            id = 4,
        )
        val BirdsEye = PlantType(
            displayName = "Bird's Eye",
            chromosome = Chromosome(
                scovilleCount = Gene.withOneBits(bitCount = 8, random = geneRandom),
                pepperYield = Gene.withOneBits(bitCount = 1, random = geneRandom),
            ),
            phases = Phases.DEFAULT,
            id = 5,
        )
    }

    val yield: Long
        get() {
            return chromosome.pepperYield.popCount() * 10L
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

        if (displayName != other.displayName) return false
        // TODO: may want to remove this
        if (scovilles != other.scovilles) return false
        if (phases != other.phases) return false
        // TODO: may want to remove this
        if (cost != other.cost) return false

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

    fun harvest() = 15L * lightStrength * mediumEffectiveness
}

data class Seed(val plantType: PlantType)