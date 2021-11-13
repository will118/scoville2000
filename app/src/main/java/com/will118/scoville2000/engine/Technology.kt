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
    val repeatablyPurchasable: Boolean,
    override val cost: Currency?,
    override val displayName: String) : Describe, Purchasable {
    AutoPlanter(
        cost = Currency(total = 75_000_000),
        displayName = "AutoPlanter",
        visibilityLevel = TechnologyLevel.Basic,
        repeatablyPurchasable = true,
    ),
    AutoHarvester(
        cost = Currency(total = 200_000_000),
        displayName = "AutoHarvester",
        visibilityLevel = TechnologyLevel.Basic,
        repeatablyPurchasable = false,
    ),
    ScovilleDistillery(
        cost = Currency(total = 500_000_000),
        displayName = "Scoville Distillery",
        visibilityLevel = TechnologyLevel.Intermediate,
        repeatablyPurchasable = false,
    ),
    ChimoleonGenetics(
        cost = Currency(total = 2_100_000_000),
        displayName = "Chimoleon Genetics",
        visibilityLevel = TechnologyLevel.Advanced,
        repeatablyPurchasable = false,
    ),
    TemporalDistortionField(
        cost = Currency(total = 999_999_999_999),
        displayName = "Temporal Distortion Field",
        visibilityLevel = TechnologyLevel.Quantum,
        repeatablyPurchasable = false,
    );
}
