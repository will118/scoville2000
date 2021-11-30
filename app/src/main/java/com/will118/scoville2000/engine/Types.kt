package com.will118.scoville2000.engine

interface Upgradable<T> {
    val upgrades: List<T>
}

interface Describe {
    val displayName: String
}

interface Purchasable {
    val cost: Currency?
}
