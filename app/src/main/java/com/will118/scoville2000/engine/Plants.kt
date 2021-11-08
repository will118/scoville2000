package com.will118.scoville2000.engine

import Currency
import Describe
import Purchasable
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.Instant

@Serializable
enum class PlantType(
    override val displayName: String,
    val scovilles: Long,
    val phases: Phases,
    override val cost: Currency?, // For seeds
): Describe, Purchasable {
    BellPepper(
        displayName = "Bell Pepper",
        scovilles = 0,
        phases = Phases.DEFAULT,
        cost = Currency(2),
    ),
    Poblano(
        displayName = "Poblano",
        scovilles = 2_000,
        phases = Phases.DEFAULT,
        cost = Currency(20),
    ),
    Guajillo(
        displayName = "Guajillo",
        scovilles = 3_000,
        phases = Phases.DEFAULT,
        cost = Currency(50),
    ),
    Jalapeno(
        displayName = "Jalapeño",
        scovilles = 6_000,
        phases = Phases.DEFAULT,
        cost = Currency(100),
    ),
    BirdsEye(
        displayName = "Bird's Eye",
        scovilles = 75_000,
        phases = Phases.DEFAULT,
        cost = Currency(200),
    ),
    Evolcano(
        displayName = "Evolcano",
        scovilles = 100_000,
        phases = Phases.DEFAULT,
        cost = null,
    );

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

    fun harvest() = 15L * lightStrength * mediumEffectiveness
}

data class Seed(val plantType: PlantType)
