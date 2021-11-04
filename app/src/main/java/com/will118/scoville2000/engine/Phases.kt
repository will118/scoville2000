package com.will118.scoville2000.engine

import java.time.Duration

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

data class Phases(
    val sprout: Duration,
    val seedling: Duration,
    val vegetative: Duration,
    val budding: Duration,
    val flowering: Duration,
    val ripening: Duration,
) {
    companion object {
        val DEFAULT = Phases(
            sprout = Duration.ofDays(7),
            seedling = Duration.ofDays(20),
            vegetative = Duration.ofDays(40),
            budding = Duration.ofDays(7),
            flowering = Duration.ofDays(7),
            ripening = Duration.ofDays(7),
        )
    }

    private val orderedPhases = listOf(
        Pair(sprout, PhaseNames.Sprout),
        Pair(seedling, PhaseNames.Seedling),
        Pair(vegetative, PhaseNames.Vegetative),
        Pair(budding, PhaseNames.Budding),
        Pair(flowering, PhaseNames.Flowering),
        Pair(ripening, PhaseNames.Ripening),
    )

    fun currentPhase(elapsed: Duration): PhaseNames? {
        var remaining = elapsed

        for ((phase, phaseName) in orderedPhases) {
            remaining = remaining.minus(phase)
            if (remaining.isNegative || remaining.isZero) {
                return phaseName
            }
        }

        return null
    }
}
