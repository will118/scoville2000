package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable

interface Upgradable<T> {
    val upgrades: List<T>
}

@Serializable
enum class Tool(
    override val displayName: String,
    override val cost: Currency?,
): Describe, Purchasable {
    None(
        displayName = "None",
        cost = null,
    ),
    Scythe(
        displayName = "Scythe",
        cost = Currency(total = 1_000_000),
    );
}


@Serializable
enum class Area(
    val dimension: Int,
    override val displayName: String,
    override val cost: Currency?,
): Describe, Purchasable, Upgradable<Area> {
    WindowSill(
        dimension = 1,
        displayName = "Window sill",
        cost = null,
    ),
    Bedroom(
        dimension = 2,
        displayName = "Bedroom",
        cost = Currency(50_000),
    ),
    SpareRoom(
        dimension = 4,
        displayName = "Spare Room",
        cost = Currency(600_000)
    ),
    Apartment(
        dimension = 8,
        displayName = "Apartment",
        cost = Currency(1_300_000)
    ),
    Warehouse(
        dimension = 16,
        displayName = "Warehouse",
        cost = Currency(90_000_000),
    );

    override val upgrades: List<Area>
        get() = values()
            .dropWhile { it.ordinal <= this.ordinal }
            .take(1)

    val total = dimension * dimension
}

interface Describe {
    val displayName: String
}

interface Purchasable {
    val cost: Currency?
}

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
        cost = Currency(10_000L),
    ),
    Halogen(
        strength = 5,
        joulesPerCostTick = 10,
        displayName = "Halogen",
        cost = Currency(350_000L),
    ),
    LED(
        strength = 5,
        joulesPerCostTick = 1,
        displayName = "LED",
        cost = Currency(4_200_000L),
    );

    override val upgrades: List<Light>
        get() = values()
            .dropWhile { it.ordinal <= this.ordinal }
            .take(1)
}

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
        displayName = "Soil + Perlite",
        cost = Currency(1_000),
    ),
    Hydroponics(
        effectiveness = 5,
        litresPerCostTick = 1,
        displayName = "Hydroponics",
        cost = Currency(200_000),
    );

    override val upgrades: List<Medium>
        get() = values()
            .dropWhile { it.ordinal <= this.ordinal }
            .take(1)
}

enum class Costs(val cost: Int) {
    ElectricityJoule(cost = 3),
    WaterLitre(cost = 1)
}

@Serializable
data class Currency(val total: Long) {
    override fun toString(): String {
        return "₡${"%,d".format(total)}"
    }
}