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
        displayName = "Ambient Light",
        cost = null,
    ),
    CFL(
        strength = 2,
        joulesPerCostTick = 1,
        displayName = "CFL",
        cost = Currency(5_000L),
    ),
    Halogen(
        strength = 5,
        joulesPerCostTick = 10,
        displayName = "Halogen",
        cost = Currency(350_000L),
    ),
    LED(
        strength = 7,
        joulesPerCostTick = 1,
        displayName = "LED",
        cost = Currency(4_200_000L),
    );

    override val upgrades: List<Light>
        get() = values()
            .dropWhile { it.ordinal <= this.ordinal }
            .take(1)
}

