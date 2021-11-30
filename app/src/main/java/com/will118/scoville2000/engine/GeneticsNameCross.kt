package com.will118.scoville2000.engine

import java.lang.Integer.min
import kotlin.math.roundToInt

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

fun nameCross(chromosome: Chromosome): String {
    val strength = min(
        NAMES.peppers.size - 1,
        chromosome.scovilleCount
            .popCount()
            .toFloat()
            .div(Gene.SIZE_BITS)
            .times(NAMES.peppers.size)
            .roundToInt()
    )

    val adj = min(
        NAMES.adjectives.size - 1,
        chromosome.totalPopCount
            .toFloat()
            .div(Chromosome.TOTAL_BITS)
            .times(NAMES.adjectives.size)
            .roundToInt()
    )

    val adjective = NAMES.adjectives[adj]
    val pepper = NAMES.peppers[strength]

    return "$adjective $pepper"
}


