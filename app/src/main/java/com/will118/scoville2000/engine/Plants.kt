package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.Instant
import kotlin.math.pow

@Serializable
data class PlantType(
    override val displayName: String,
    val phases: Phases,
    val chromosome: Chromosome,
    override val cost: Currency?, // For seeds
    val autoPlantChecked: Boolean = false,
): Describe, Purchasable {
    companion object {
        // TODO: would be nice to derive from code
        const val TOTAL_PEPPER_TYPES = 99

        val BellPepper = PlantType(
            displayName = "Bell Pepper",
//            scovilles = Scovilles(0),
            phases = Phases.DEFAULT,
            chromosome = Chromosome(
                pepperYield = Gene(lsb = flipBits(1), msb = flipBits(1)),
            ),
            cost = Currency(2),
        )
        val Poblano = PlantType(
            displayName = "Poblano",
            chromosome = Chromosome(
                scovilleCount = Gene(lsb = flipBits(1), msb = flipBits(1)),
                pepperYield = Gene(lsb = flipBits(2), msb = flipBits(1)),
            ),
            phases = Phases.DEFAULT,
            cost = Currency(20),
        )
        val Guajillo = PlantType(
            displayName = "Guajillo",
            chromosome = Chromosome(
                scovilleCount = Gene(lsb = flipBits(2), msb = flipBits(1)),
                pepperYield = Gene(lsb = flipBits(1), msb = flipBits(1)),
            ),
            phases = Phases.DEFAULT,
            cost = Currency(50),
        )
        val Jalapeno = PlantType(
            displayName = "Jalapeño",
            chromosome = Chromosome(
                scovilleCount = Gene(lsb = flipBits(2), msb = flipBits(2)),
                pepperYield = Gene(lsb = flipBits(1), msb = flipBits(1)),
            ),
            phases = Phases.DEFAULT,
            cost = Currency(100),
        )
        val BirdsEye = PlantType(
            displayName = "Bird's Eye",
            chromosome = Chromosome(
                scovilleCount = Gene(lsb = flipBits(4), msb = flipBits(4)),
                pepperYield = Gene(lsb = flipBits(1), msb = flipBits(1)),
            ),
            phases = Phases.DEFAULT,
            cost = Currency(200),
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