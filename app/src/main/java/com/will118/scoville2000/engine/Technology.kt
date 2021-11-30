package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable

@Serializable
enum class TechnologyLevel {
    None,
    Amateur,
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
    Chillidex(
        cost = Currency(total = 250_000),
        displayName = "ChilliDex",
        visibilityLevel = TechnologyLevel.Amateur,
        repeatablyPurchasable = false,
    ),
    AutoPlanter(
        cost = Currency(total = 5_000_000),
        displayName = "AutoPlanter",
        visibilityLevel = TechnologyLevel.Basic,
        repeatablyPurchasable = true,
    ),
    AutoHarvester(
        cost = Currency(total = 10_000_000),
        displayName = "AutoHarvester",
        visibilityLevel = TechnologyLevel.Basic,
        repeatablyPurchasable = false,
    ),
    ScovilleDistillery(
        cost = Currency(total = 50_000_000),
        displayName = "Scoville Distillery",
        visibilityLevel = TechnologyLevel.Intermediate,
        repeatablyPurchasable = false,
    ),
    ChimoleonGenetics(
        cost = Currency(total = 2_000_000_000),
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
