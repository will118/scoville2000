package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable

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
        cost = Currency(5_000),
    ),
    SpareRoom(
        dimension = 4,
        displayName = "Spare Room",
        cost = Currency(50_000)
    ),
    Apartment(
        dimension = 8,
        displayName = "Apartment",
        cost = Currency(1_000_000)
    ),
    Warehouse(
        dimension = 16,
        displayName = "Warehouse",
        cost = Currency(90_000_000),
    ),
    BusinessPark(
        dimension = 32,
        displayName = "Business Park",
        cost = Currency(1_000_000_000),
    );

    override val upgrades: List<Area>
        get() = values()
            .dropWhile { it.ordinal <= this.ordinal }
            .take(1)

    val total = dimension * dimension
}