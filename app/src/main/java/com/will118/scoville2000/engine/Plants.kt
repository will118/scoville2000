package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.Instant

@Serializable
data class PlantType(
    override val displayName: String,
    val scovilles: Scovilles,
    val phases: Phases,
    override val cost: Currency?, // For seeds
    val autoPlantChecked: Boolean = false,
): Describe, Purchasable {
    companion object {
        val BellPepper = PlantType(
            displayName = "Bell Pepper",
            scovilles = Scovilles(0),
            phases = Phases.DEFAULT,
            cost = Currency(2),
        )
        val Poblano = PlantType(
            displayName = "Poblano",
            scovilles = Scovilles(2_000),
            phases = Phases.DEFAULT,
            cost = Currency(20),
        )
        val Guajillo = PlantType(
            displayName = "Guajillo",
            scovilles = Scovilles(3_000),
            phases = Phases.DEFAULT,
            cost = Currency(50),
        )
        val Jalapeno = PlantType(
            displayName = "Jalapeño",
            scovilles = Scovilles(6_000),
            phases = Phases.DEFAULT,
            cost = Currency(100),
        )
        val BirdsEye = PlantType(
            displayName = "Bird's Eye",
            scovilles = Scovilles(75_000),
            phases = Phases.DEFAULT,
            cost = Currency(200),
        )
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
