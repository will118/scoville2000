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
//    var balance: Currency = Currency(80),
    var balance: Currency = Currency(80000000000000000),
    var area: Area = Area.WindowSill,
    var light: Light = Light.Ambient,
    var medium: Medium = Medium.Soil,
    var tool: Tool = Tool.None,
//    var technologyLevel: TechnologyLevel = TechnologyLevel.None,
    var technologyLevel: TechnologyLevel = TechnologyLevel.Quantum,
    var autoHarvestEnabled: Boolean = false,
    var milliCounter: Long = 0, // Used for calculating costs
    val epochMillis: Long = Instant.now().toEpochMilli(), // Used to determine progression
    var dateMillis: Long = epochMillis,
    val plantPots: List<PlantPot> = List(area.total) { PlantPot(plant = null) },
    val pepperInventory: List<Pair<PlantType, StockLevel>> = listOf(
        Pair(PlantType.BellPepper, StockLevel(quantity = 5)),
    ),
    val distillateInventory: List<Pair<Distillate, FractionalStockLevel>> = emptyList(),
    val technologies: List<Technology> = emptyList(),
    val plantTypes: List<PlantType> = listOf(
        PlantType.BellPepper,
    ),
    val geneticComputationState: GeneticComputationState = GeneticComputationState(
        leftPlantType = PlantType.BellPepper,
        rightPlantType = PlantType.BellPepper,
        isActive = false,
        population = emptyList(),
        fitnessFunction = FitnessFunction(),
    )
)