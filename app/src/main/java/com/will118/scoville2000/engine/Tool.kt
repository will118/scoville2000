package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable

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
