package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable

@Serializable
enum class Medium(
    val effectiveness: Int,
    val litresPerCostTick: Int,
    override val displayName: String,
    override val cost: Currency?,
): Describe, Purchasable, Upgradable<Medium> {
    Soil(
        effectiveness = 2,
        litresPerCostTick = 1,
        displayName = "Soil",
        cost = null // TODO make it so you have to buy everything
    ),
    SoilPerlite(
        effectiveness = 3,
        litresPerCostTick = 1,
        displayName = "Soil & Perlite",
        cost = Currency(500),
    ),
    Hydroponics(
        effectiveness = 5,
        litresPerCostTick = 1,
        displayName = "Hydroponics",
        cost = Currency(50_000),
    );

    override val upgrades: List<Medium>
        get() = values()
            .dropWhile { it.ordinal <= this.ordinal }
            .take(1)
}

