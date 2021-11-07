package com.will118.scoville2000.engine

import Area
import Light
import Medium
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class GameStateData(
    var balance: Long = 150,
    var area: Area = Area.SpareRoom,
    var light: Light = Light.Ambient,
    var medium: Medium = Medium.Soil,
    var milliCounter: Long = 0, // Used for calculating costs
    var dateMillis: Long = Instant.now().toEpochMilli(),
    val plantPots: List<PlantPot> = List(area.total) { PlantPot(plant = null) },
    val inventory: List<Pair<PlantType, StockLevel>> = listOf(
        Pair(PlantType.BellPepper, StockLevel(peppers = 5)),
        Pair(PlantType.Poblano, StockLevel(peppers = 5)),
    ),
)