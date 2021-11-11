package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*

@Serializable
data class GameId(val msb: Long, val lsb: Long) {
    companion object {
        fun fromUUID(uuid: UUID) = GameId(
            msb = uuid.mostSignificantBits,
            lsb = uuid.leastSignificantBits,
        )
    }
}

@Serializable
data class GameStateData(
    val id: GameId = GameId.fromUUID(UUID.randomUUID()),
    var balance: Currency = Currency(80),
    var area: Area = Area.WindowSill,
    var light: Light = Light.Ambient,
    var medium: Medium = Medium.Soil,
    var technologyLevel: TechnologyLevel = TechnologyLevel.None,
    var milliCounter: Long = 0, // Used for calculating costs
    var dateMillis: Long = Instant.now().toEpochMilli(),
    val plantPots: List<PlantPot> = List(area.total) { PlantPot(plant = null) },
    val inventory: List<Pair<PlantType, StockLevel>> = listOf(
        Pair(PlantType.BellPepper, StockLevel(peppers = 5)),
    ),
)