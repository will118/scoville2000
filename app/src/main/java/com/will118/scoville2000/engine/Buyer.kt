package com.will118.scoville2000.engine

import Currency
import java.lang.Long.max

enum class Buyer(val pricePerScoville: Currency) {
    Friends(pricePerScoville = Currency(1)),
    Club(pricePerScoville = Currency(2)),
    LocalShop(pricePerScoville = Currency(4));
//    Exchange(pricePerScoville = )

    fun total(plantType: PlantType, peppers: Long) =
        2.plus(max(plantType.scovilles / 1000, 1) * pricePerScoville.total) * peppers
}