package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable
import java.time.Duration
import kotlin.math.roundToInt

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
        fun ofScale(scale: Float = 1.0f) = Phases(
            sproutDays = (7 * scale).roundToInt(),
            seedlingDays = (20 * scale).roundToInt(),
            vegetativeDays = (40 * scale).roundToInt(),
            buddingDays = (3 * scale).roundToInt(),
            floweringDays = (4 * scale).roundToInt(),
            ripeningDays = (7 * scale).roundToInt(),
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

    val totalDuration = orderedPhases.sumOf { it.first }

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
