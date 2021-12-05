package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable

@Serializable
data class Currency(val total: Long) {
    override fun toString(): String {
        return "₡${fmtLong(total)}"
    }

    fun canAfford(price: Currency) = total >= price.total
}
