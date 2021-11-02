package com.will118.gt2000.engine

import Currency
import Describe
import Purchasable
import java.time.Duration
import java.time.Instant

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
        scovilles = 1_250,
        phases = Phases.DEFAULT,
        cost = Currency(2),
    ),
    Guajillo(
        displayName = "Guajillo",
        scovilles = 3_000,
        phases = Phases.DEFAULT,
        cost = Currency(5),
    ),
    Jalapeno(
        displayName = "Jalapeño",
        scovilles = 6_000,
        phases = Phases.DEFAULT,
        cost = Currency(10),
    ),
    BirdsEye(
        displayName = "Bird's Eye",
        scovilles = 75_000,
        phases = Phases.DEFAULT,
        cost = Currency(20),
    ),
    Evolcano(
        displayName = "Evolcano",
        scovilles = 100_000,
        phases = Phases.DEFAULT,
        cost = null,
    );

    fun toSeed() = Seed(this)
}

data class Plant(val plantType: PlantType, val epoch: Instant) {
    fun currentPhase(currentInstant: Instant) =
        plantType.phases.currentPhase(Duration.between(epoch, currentInstant))

    fun isRipe(currentInstant: Instant) = currentPhase(currentInstant)?.isRipe ?: false

    fun harvest() = 15L
}

data class Seed(val plantType: PlantType)
