package com.will118.scoville2000.engine

import java.lang.Long.max

enum class Membership(
    val pricePerScoville: Currency,
    override val displayName: String,
    override val cost: Currency?,
) : Describe, Purchasable, Upgradable<Membership> {
    Friends(
        pricePerScoville = Currency(1),
        displayName = "Friends",
        cost = null,
    ),
    Club(
        pricePerScoville = Currency(2),
        displayName = "Local Chilli Club",
        cost = Currency(total = 150_000)
    ),
    HeatExchange(
        pricePerScoville = Currency(4),
        displayName = "Heat Exchange",
        cost = Currency(total = 500_000_000)
    );

    fun total(plantType: PlantType, quantity: Long) =
        2.plus(max(plantType.scovilles.count / 1000, 1) * pricePerScoville.total) * quantity

    // Bit dodge, but is currently fine because you can only sell distillates that never change in value.
    fun total(distillateType: DistillateType, quantity: Long) =
        (distillateType.baseScovilles.count / 1000 * pricePerScoville.total) * quantity * distillateType.priceMultiplier

    override val upgrades: List<Membership>
        get() = Membership.values()
            .dropWhile { it.ordinal <= this.ordinal }
            .take(1)
}