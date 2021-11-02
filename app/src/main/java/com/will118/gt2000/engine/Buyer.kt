package com.will118.gt2000.engine

import java.lang.Long.max

enum class Buyer(val pricePerScoville: Long) {
    Friends(pricePerScoville = 1),
    Club(pricePerScoville = 2),
    LocalShop(pricePerScoville = 4);
//    Exchange(pricePerScoville = )

    fun total(plantType: PlantType, peppers: Long) =
        (max(plantType.scovilles, 1) * pricePerScoville) * peppers
}