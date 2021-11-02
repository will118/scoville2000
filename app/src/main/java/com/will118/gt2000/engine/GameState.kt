package com.will118.gt2000.engine

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
import java.time.Instant

data class StockLevel(var seeds: Long, var peppers: Long)

class GameState {
    private companion object {
        const val TAG = "GameState"
    }

    private var dirtyBalance = 10000000L
    private val _balance = MutableLiveData(dirtyBalance)
    val balance: LiveData<Long> by ::_balance

    private val _inventory = mutableStateMapOf<PlantType, StockLevel>()
    val inventory: SnapshotStateMap<PlantType, StockLevel> by ::_inventory

    init {
        _inventory[PlantType.BellPepper] = StockLevel(seeds = 1, peppers = 5)
        _inventory[PlantType.Evolcano] = StockLevel(seeds = 10, peppers = 500)
    }

    private val _plants = mutableStateListOf<Plant>()
    val plants: SnapshotStateList<Plant> by ::_plants

    private var dirtyArea = Area.WindowSill
    private val _area = mutableStateOf(dirtyArea)
    val area: State<Area> by ::_area

    private var dirtyLight = Light.Ambient
    private val _light = mutableStateOf(dirtyLight)
    val light: State<Light> by ::_light

    private var dirtyMedium = Medium.Soil
    private val _medium = mutableStateOf(dirtyMedium)
    val medium: State<Medium> by ::_medium

    var buyer = Buyer.Friends
        private set

    private var dirtyDate = Instant.now()
    private val _date = MutableLiveData(dirtyDate)
    val date: LiveData<Instant> by ::_date

    private fun calculateIncome() = 0

    private fun calculateCosts() =
        (dirtyLight.joulesPerTick * Costs.ElectricityJoule.cost)
        .plus(dirtyMedium.litresPerTick * Costs.WaterLitre.cost)

    fun harvest(plant: Plant) {
        if (plant.isRipe(dirtyDate)) {
            _inventory.compute(plant.plantType) { _, stock ->
                return@compute if (stock != null) {
                    stock.peppers += plant.harvest()
                    stock
                } else {
                    StockLevel(seeds = 0, peppers = plant.harvest())
                }
            }
            plants.remove(plant)
        }
    }

    fun compost(plant: Plant) = plants.remove(plant)

    fun sellProduce(plantType: PlantType) {
        _inventory[plantType]?.let {
            dirtyBalance += buyer.total(plantType = plantType, peppers = it.peppers)
            it.peppers = 0
            _balance.postValue(dirtyBalance)
        }
    }

    fun buyLightUpgrade(desiredLight: Light) {
        if (deductPurchaseCost(desiredLight)) {
            dirtyLight = desiredLight
            _light.value = desiredLight
        }
    }

    fun buyMediumUpgrade(desiredMedium: Medium) {
        if (deductPurchaseCost(desiredMedium)) {
            dirtyMedium = desiredMedium
            _medium.value = desiredMedium
        }
    }

    private fun deductPurchaseCost(upgrade: Purchasable): Boolean {
        val cost = upgrade.cost!!.total
        if (dirtyBalance >= cost) {
            dirtyBalance -= cost
            return true
        }

        return false
    }

    fun plantSeed(seed: Seed) {
        if (_plants.size < dirtyArea.total) {
            if (deductPurchaseCost(seed.plantType)) {
                _plants.add(
                    Plant(
                        plantType = seed.plantType,
                        epoch = dirtyDate,
                    )
                )
            }
        }
    }

    fun onTick(): Boolean {
        // Add income first
        calculateIncome().also {
            dirtyBalance += it
        }

        calculateCosts().also {
            if (it > dirtyBalance) {
                return true
            }

            //dirtyBalance -= it
        }

        _balance.postValue(dirtyBalance)

        dirtyDate = dirtyDate.plusSeconds(2000)
        _date.postValue(dirtyDate)

        return false
    }
}