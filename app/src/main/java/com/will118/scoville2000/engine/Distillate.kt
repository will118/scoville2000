package com.will118.scoville2000.engine

data class Scovilles(val count: Long) {
    override fun toString(): String {
        return "${"%,d".format(count)}"
    }
}

enum class Distillate(
    val requiredScovilles: Scovilles,
    val priceMultiplier: Long,
    override val displayName: String,
): Describe {
    ChilliOil(
        requiredScovilles = Scovilles(15_000_000),
        priceMultiplier = 2,
        displayName = "Chilli Oil",
    ),
    HotSauce(
        requiredScovilles = Scovilles(90_000_000),
        priceMultiplier = 4,
        displayName = "Hot Sauce",
    ),
    QuantumCapsicum(
        requiredScovilles = Scovilles(25_000_000_000),
        priceMultiplier = 0,
        displayName = "Quantum Capsicum",
    ),
}