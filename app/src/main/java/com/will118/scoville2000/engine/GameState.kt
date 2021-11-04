package com.will118.scoville2000.engine

import Area
import Costs
import Light
import Medium
import Purchasable
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*

@Serializable
data class StockLevel(var seeds: Long, var peppers: Long)

@Serializable
data class GameStateData(
    var balance: Long = 10000000L,
    var area: Area = Area.SpareRoom,
    var light: Light = Light.Ambient,
    var medium: Medium = Medium.Soil,
    var dateMillis: Long = Instant.now().toEpochMilli(),
    val plants: List<Plant?> = Collections.nCopies(area.total, null as Plant?),
    val inventory: List<Pair<PlantType, StockLevel>> = listOf(
        Pair(PlantType.BellPepper, StockLevel(seeds = 1, peppers = 5)),
        Pair(PlantType.Evolcano, StockLevel(seeds = 10, peppers = 500))
    ),
)

class GameState(private val gameStateData: GameStateData) {
    private val _balance = MutableLiveData(gameStateData.balance)
    val balance: LiveData<Long> by ::_balance

    private val _inventory = gameStateData.inventory.toMutableStateMap()
    val inventory: SnapshotStateMap<PlantType, StockLevel> by ::_inventory

    private val _area = mutableStateOf(gameStateData.area)
    val area: State<Area> by ::_area

    private val _plants = gameStateData.plants.toMutableStateList()
    val plants: SnapshotStateList<Plant?> by ::_plants

    private val _light = mutableStateOf(gameStateData.light)
    val light: State<Light> by ::_light

    private val _medium = mutableStateOf(gameStateData.medium)
    val medium: State<Medium> by ::_medium

    // Using LiveData to get around an issue at startup where we try and read a value
    // before it is snapshot.
    private val _dateMillis = MutableLiveData(gameStateData.dateMillis)
    val dateMillis: LiveData<Long> by ::_dateMillis

    var buyer = Buyer.Friends
        private set

    private fun calculateCosts() =
        (gameStateData.light.joulesPerTick * Costs.ElectricityJoule.cost)
        .plus(gameStateData.medium.litresPerTick * Costs.WaterLitre.cost)

    fun harvest(plant: Plant) {
        if (plant.isRipe(gameStateData.dateMillis)) {
            _inventory.compute(plant.plantType) { _, stock ->
                return@compute if (stock != null) {
                    stock.peppers += plant.harvest()
                    stock
                } else {
                    StockLevel(seeds = 0, peppers = plant.harvest())
                }
            }
            _plants[_plants.indexOf(plant)] = null
        }
    }

    fun compost(plant: Plant) {
        _plants[_plants.indexOf(plant)] = null
    }


    fun sellProduce(plantType: PlantType) {
        _inventory[plantType]?.let {
            gameStateData.balance += buyer.total(plantType = plantType, peppers = it.peppers)
            it.peppers = 0
            _balance.postValue(gameStateData.balance)
        }
    }

    fun buyLightUpgrade(desiredLight: Light) {
        if (deductPurchaseCost(desiredLight)) {
            gameStateData.light = desiredLight
            _light.value = desiredLight
        }
    }

    fun buyMediumUpgrade(desiredMedium: Medium) {
        if (deductPurchaseCost(desiredMedium)) {
            gameStateData.medium = desiredMedium
            _medium.value = desiredMedium
        }
    }

    fun buyAreaUpgrade(desiredArea: Area) {
        if (deductPurchaseCost(desiredArea)) {
            _plants.addAll(Collections.nCopies(desiredArea.total - gameStateData.area.total, null))
            gameStateData.area = desiredArea
            _area.value = desiredArea
        }
    }

    private fun deductPurchaseCost(upgrade: Purchasable): Boolean {
        val cost = upgrade.cost!!.total
        if (gameStateData.balance >= cost) {
            gameStateData.balance -= cost
            return true
        }

        return false
    }

    fun plantSeed(seed: Seed) {
        val index = _plants.indexOfFirst { it == null }
        if (index >= 0 && deductPurchaseCost(seed.plantType)) {
            _plants[index] = Plant(
                plantType = seed.plantType,
                epochMillis = gameStateData.dateMillis,
            )
        }
    }

    fun onTick(): Boolean {
        calculateCosts().also {
            if (it > gameStateData.balance) {
                return true
            }

            gameStateData.balance -= it
        }

        _balance.postValue(gameStateData.balance)

        gameStateData.dateMillis += 2000 * 1000
        _dateMillis.postValue(gameStateData.dateMillis)

        return false
    }

    fun snapshot() = gameStateData.copy(
        plants = _plants.toList(),
        inventory = _inventory.toList(),
    )
}