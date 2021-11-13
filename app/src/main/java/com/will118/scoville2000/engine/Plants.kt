package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.Instant

@Serializable
data class PlantType(
    override val displayName: String,
    val scovilles: Long,
    val phases: Phases,
    override val cost: Currency?, // For seeds
    val autoPlantChecked: Boolean = false,
): Describe, Purchasable {
    companion object {
        val BellPepper = PlantType(
            displayName = "Bell Pepper",
            scovilles = 0,
            phases = Phases.DEFAULT,
            cost = Currency(2),
        )
        val Poblano = PlantType(
            displayName = "Poblano",
            scovilles = 2_000,
            phases = Phases.DEFAULT,
            cost = Currency(20),
        )
        val Guajillo = PlantType(
            displayName = "Guajillo",
            scovilles = 3_000,
            phases = Phases.DEFAULT,
            cost = Currency(50),
        )
        val Jalapeno = PlantType(
            displayName = "Jalapeño",
            scovilles = 6_000,
            phases = Phases.DEFAULT,
            cost = Currency(100),
        )
        val BirdsEye = PlantType(
            displayName = "Bird's Eye",
            scovilles = 75_000,
            phases = Phases.DEFAULT,
            cost = Currency(200),
        )
    }

    fun toSeed() = Seed(this)
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
