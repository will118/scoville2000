package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable

@Serializable
enum class TechnologyLevel {
    None,
    Basic,
    Intermediate,
    Advanced,
    Quantum;

    fun visibleTechnologies() = Technology.values().filter { it.visibilityLevel <= this }
}

@Serializable
enum class Technology(
    val visibilityLevel: TechnologyLevel,
    override val cost: Currency?,
    override val displayName: String) : Describe, Purchasable {
    AutoHarvester(
        cost = Currency(total = 25_000_000),
        displayName = "AutoHarvester",
        visibilityLevel = TechnologyLevel.Basic,
    ),
    AutoPlanter(
        cost = Currency(total = 75_000_000),
        displayName = "AutoPlanter",
        visibilityLevel = TechnologyLevel.Basic,
    ),
    ScovilleDistillery(
        cost = Currency(total = 500_000_000),
        displayName = "Scoville Distillery",
        visibilityLevel = TechnologyLevel.Intermediate,
    ),
    ChimoleonGenetics(
        cost = Currency(total = 2_100_000_000),
        displayName = "Chimoleon Genetics",
        visibilityLevel = TechnologyLevel.Advanced,
    );

}
