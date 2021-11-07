package com.will118.scoville2000.engine

import Area
import Costs
import Light
import Medium
import Purchasable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.toMutableStateMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.serialization.Serializable
import java.time.Duration

@Serializable
data class StockLevel(var peppers: Long)

class GameState(private val gameStateData: GameStateData) {
    private companion object {
        val MILLIS_PER_TICK = Duration.ofMinutes(30).toMillis()
        val MILLIS_PER_COST_TICK = Duration.ofDays(1).toMillis()
    }

    private val _balance = MutableLiveData(gameStateData.balance)
    val balance: LiveData<Long> by ::_balance

    private val _inventory = gameStateData.inventory.toMutableStateMap()
    val inventory: SnapshotStateMap<PlantType, StockLevel> by ::_inventory

    private val _area = mutableStateOf(gameStateData.area)
    val area: State<Area> by ::_area

    private val _plantPots = gameStateData.plantPots.toMutableStateList()
    val plantPots: SnapshotStateList<PlantPot> by ::_plantPots

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

    fun harvest(plantPot: PlantPot) {
        plantPot.plant?.let { plant ->
            if (plant.isRipe(gameStateData.dateMillis)) {
                _inventory.compute(plant.plantType) { _, stock ->
                    StockLevel(
                        peppers = plant.harvest().plus(stock?.peppers ?: 0)
                    )
                }

                removePlant(plantPot = plantPot)
            }
        }
    }

    fun compost(plantPot: PlantPot) = removePlant(plantPot = plantPot)

    private fun removePlant(plantPot: PlantPot) {
        _plantPots[_plantPots.indexOf(plantPot)] = PlantPot(plant = null)
    }

    fun sellProduce(plantType: PlantType) {
        _inventory[plantType]?.let {
            _inventory[plantType] = StockLevel(peppers = 0)
            gameStateData.balance += buyer.total(plantType = plantType, peppers = it.peppers)
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
            _plantPots.addAll(
                List(desiredArea.total - gameStateData.area.total) { PlantPot(plant = null) }
            )
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
        val index = _plantPots.indexOfFirst { it.plant == null }
        if (index >= 0 && deductPurchaseCost(seed.plantType)) {
            _plantPots[index] = PlantPot(
                plant = Plant(
                    plantType = seed.plantType,
                    epochMillis = gameStateData.dateMillis,
                    lightStrength = gameStateData.light.strength,
                    mediumEffectiveness = gameStateData.medium.effectiveness,
                )
            )
        }
    }

    private fun calculateCosts(): Long {
        val light = gameStateData.light
        val medium = gameStateData.medium

        return (light.joulesPerCostTick.toLong() * Costs.ElectricityJoule.cost)
         .plus(medium.litresPerCostTick * Costs.WaterLitre.cost)
         .times(
             _plantPots
                 .count { it.plant?.isGrowing(gameStateData.dateMillis) == true }
         )
    }

    fun onTick(): Boolean {
        gameStateData.dateMillis += MILLIS_PER_TICK
        _dateMillis.postValue(gameStateData.dateMillis)

        gameStateData.milliCounter += MILLIS_PER_TICK

        if (gameStateData.milliCounter >= MILLIS_PER_COST_TICK) {
            gameStateData.milliCounter -= MILLIS_PER_COST_TICK

            calculateCosts().also {
                if (it > gameStateData.balance) {
                    return true
                }

                gameStateData.balance -= it
            }
        }

        _balance.postValue(gameStateData.balance)

        return false
    }

    fun snapshot() = gameStateData.copy(
        plantPots = _plantPots.toList(),
        inventory = _inventory.toList().map { it.copy(second = it.second.copy()) },
    )
}