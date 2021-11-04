package com.will118.scoville2000.engine

import Area
import Costs
import Light
import Medium
import Purchasable
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.time.Instant
import java.util.*

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

    private var dirtyArea = Area.SpareRoom
    private val _area = mutableStateOf(dirtyArea)
    val area: State<Area> by ::_area

    private val _plants = Collections.nCopies(dirtyArea.total, null as Plant?).toMutableStateList()
    val plants: SnapshotStateList<Plant?> by ::_plants

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
            Log.i(TAG, "Harvested: $plant")
            _plants[_plants.indexOf(plant)] = null
        }
    }

    fun compost(plant: Plant) {
        _plants[_plants.indexOf(plant)] = null
    }


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

    fun buyAreaUpgrade(desiredArea: Area) {
        if (deductPurchaseCost(desiredArea)) {
            _plants.addAll(Collections.nCopies(desiredArea.total - dirtyArea.total, null))
            dirtyArea = desiredArea
            _area.value = desiredArea
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
        val index = _plants.indexOfFirst { it == null }
        if (index >= 0 && deductPurchaseCost(seed.plantType)) {
            _plants[index] = Plant(
                plantType = seed.plantType,
                epoch = dirtyDate,
                position = 1
            )
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