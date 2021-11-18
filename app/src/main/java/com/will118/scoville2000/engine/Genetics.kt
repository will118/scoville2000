package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable

@Serializable
enum class GeneticTrait {
    PepperYield,
    ScovilleCount,
    HallucinogenicLevel,
    GrowthDuration,
}

@Serializable
data class Gene(
    private val lsb: Long,
    private val msb: Long,
) {
    fun popCount() = lsb.countOneBits() + msb.countOneBits()
}

@Serializable
data class Chromosome(val genes: Map<GeneticTrait, Gene>) {
    fun fitness(goals: Map<GeneticTrait, Float>): Float {
        return goals.entries.fold(0f) { acc, entry ->
            genes[entry.key]?.let {
                acc * it.popCount()
            } ?: acc
        }
    }
}



