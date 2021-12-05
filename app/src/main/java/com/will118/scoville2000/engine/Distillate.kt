package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable
import kotlin.math.pow

@Serializable
data class Scovilles(val count: Long) {
    override fun toString() = fmtLong(count)

    fun increase(percentage: Int) = copy(
        count = count + count.div(100).times(percentage)
    )
}

@Serializable
enum class DistillateType(
    val baseScovilles: Scovilles,
    val priceMultiplier: Long,
    override val displayName: String
) : Describe {
    ChilliOil(
        baseScovilles = Scovilles(15_000_000),
        priceMultiplier = 2,
        displayName = "Chilli Oil",
    ),
    HotSauce(
        displayName = "Hot Sauce",
        baseScovilles = Scovilles(90_000_000),
        priceMultiplier = 4,
    ),
    QuantumCapsicum(
        displayName = "Quantum Capsicum",
        baseScovilles = Scovilles(1_000_000_000),
        priceMultiplier = 0,
    ),
}

@Serializable
data class Distillate(
    val generation: Int = 1,
    val type: DistillateType,
): Describe {
    companion object {
        val ChilliOil = Distillate(
            type = DistillateType.ChilliOil,
        )

        val HotSauce = Distillate(
            type = DistillateType.HotSauce,
        )

        val QuantumCapsicum = Distillate(
            type = DistillateType.QuantumCapsicum,
        )
    }

    override val displayName: String = type.displayName

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Distillate
        if (type != other.type) return false
        return true
    }

    val requiredScovilles = when (type) {
        DistillateType.QuantumCapsicum -> type.baseScovilles.count
            .toDouble()
            .times(1.1.pow(generation))
            .toLong()
        else -> type.baseScovilles.count
    }

    fun next() = this.copy(generation = generation + 1)
}