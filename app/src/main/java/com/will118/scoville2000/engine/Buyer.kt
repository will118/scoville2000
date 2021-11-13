package com.will118.scoville2000.engine

import java.lang.Long.max

enum class Buyer(
    val pricePerScoville: Currency,
    override val displayName: String) : Describe {
    Friends(
        pricePerScoville = Currency(1),
        displayName = "friends",
    ),
    Club(
        pricePerScoville = Currency(2),
        displayName = "club",
    ),
    LocalShop(
        pricePerScoville = Currency(4),
        displayName = "shop"
    );
//    Exchange(pricePerScoville = )

    fun total(plantType: PlantType, quantity: Long) =
        2.plus(max(plantType.scovilles / 1000, 1) * pricePerScoville.total) * quantity

    fun total(distillate: Distillate, quantity: Long) =
        (distillate.requiredScovilles.count / 1000 * pricePerScoville.total) * quantity * distillate.priceMultiplier
}