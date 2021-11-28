package com.will118.scoville2000.engine

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*

@Serializable
data class ObjectId(val hi: Long, val lo: Long) {
    companion object {
        private fun fromUUID(uuid: UUID) = ObjectId(
            hi = uuid.mostSignificantBits,
            lo = uuid.leastSignificantBits,
        )
        fun random() = fromUUID(UUID.randomUUID())
    }
}

@Serializable
data class GameStateData(
    val id: ObjectId = ObjectId.random(),
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
    val geneticComputationState: GeneticComputationState = GeneticComputationState.default()
) {
    fun printHashCodes() {
        println(
        "   snap: ${hashCode()}\n" +
        "   inventory: ${pepperInventory.size}\n" +
        "   geneticState: ${geneticComputationState.hashCode()}\n" +
        "   plantTypes: ${plantTypes.hashCode()}\n" +
        "   tech: ${technologies.hashCode()}\n" +
        "   distillate: ${distillateInventory.hashCode()}\n" +
//        "   plantTypes: ${plantTypes.hashCode()}\n" +
//        "   plantTypes: ${plantTypes.hashCode()}\n" +
        "   plantTypes: ${plantTypes.hashCode()}\n"
        )
    }
}