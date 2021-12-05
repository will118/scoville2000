package com.will118.scoville2000.engine

import java.lang.Integer.min
import kotlin.math.roundToInt

fun nameCross(chromosome: Chromosome): String {
    val strength = min(
        PlantType.Companion.NAMES.peppers.size - 1,
        chromosome.scovilleCount
            .popCount()
            .toFloat()
            .div(Gene.SIZE_BITS)
            .times(PlantType.Companion.NAMES.peppers.size)
            .roundToInt()
    )

    val adj = min(
        PlantType.Companion.NAMES.adjectives.size - 1,
        chromosome.totalPopCount
            .toFloat()
            .div(Chromosome.TOTAL_BITS)
            .times(PlantType.Companion.NAMES.adjectives.size)
            .roundToInt()
    )

    val adjective = PlantType.Companion.NAMES.adjectives[adj]
    val pepper = PlantType.Companion.NAMES.peppers[strength]

    return "$adjective $pepper"
}


