package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable

@Serializable
enum class Light(
    val strength: Int,
    val joulesPerCostTick: Int,
    override val displayName: String,
    override val cost: Currency?,
):  Describe, Purchasable, Upgradable<Light> {
    Ambient(
        strength = 1,
        joulesPerCostTick = 0,
        displayName = "Ambient",
        cost = null,
    ),
    CFL(
        strength = 2,
        joulesPerCostTick = 1,
        displayName = "CFL",
        cost = Currency(2_000L),
    ),
    Halogen(
        strength = 5,
        joulesPerCostTick = 10,
        displayName = "Halogen",
        cost = Currency(110_000L),
    ),
    LED(
        strength = 8,
        joulesPerCostTick = 1,
        displayName = "LED",
        cost = Currency(4_200_000L),
    );

    override val upgrades: List<Light>
        get() = values()
            .dropWhile { it.ordinal <= this.ordinal }
            .take(1)
}

