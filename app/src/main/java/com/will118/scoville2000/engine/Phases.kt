package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable
import java.time.Duration

@Serializable
enum class PhaseNames(val displayName: String) {
    Sprout("Sprout"),
    Seedling("Seedling"),
    Vegetative("Vegetative"),
    Budding("Budding"),
    Flowering("Flowering"),
    Ripening("Ripening");

    val isRipe
        get() = this == Ripening
}

@Serializable
data class Phases(
    private val sproutDays: Int,
    private val seedlingDays: Int,
    private val vegetativeDays: Int,
    private val buddingDays: Int,
    private val floweringDays: Int,
    private val ripeningDays: Int,
) {
    companion object {
        val DEFAULT = Phases(
            sproutDays = 7,
            seedlingDays = 20,
            vegetativeDays = 40,
            buddingDays = 3,
            floweringDays = 4,
            ripeningDays = 7,
        )
    }

    private val orderedPhases = listOf(
        Pair(sproutDays, PhaseNames.Sprout),
        Pair(seedlingDays, PhaseNames.Seedling),
        Pair(vegetativeDays, PhaseNames.Vegetative),
        Pair(buddingDays, PhaseNames.Budding),
        Pair(floweringDays, PhaseNames.Flowering),
        Pair(ripeningDays, PhaseNames.Ripening),
    )

    fun currentPhase(elapsed: Duration): PhaseNames? {
        var remaining = elapsed.toDays()

        for ((phaseDays, phaseName) in orderedPhases) {
            remaining -= phaseDays
            if (remaining <= 0) {
                return phaseName
            }
        }

        return null
    }
}
