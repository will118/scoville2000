package com.will118.scoville2000.engine

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
data class StockLevel(val quantity: Long) {
    override fun toString() = fmtLong(quantity)
}

@Serializable
data class FractionalStockLevel(val quantity: Long, val thousandths: Int) {
    override fun toString(): String {
        return when (thousandths) {
            1000 -> "${quantity + 1}.00"
            0 -> "${quantity}.00"
            else -> "${quantity}.${thousandths / 10}"
        }
    }
}

fun MutableList<() -> Boolean>.tryPop() {
    firstOrNull()?.let {
        if (it()) {
            removeAt(0)
        }
    }
}

class GameState(private val data: GameStateData) {
    private companion object {
        val MILLIS_PER_TICK = Duration.ofHours(6).toMillis()
        val MILLIS_PER_DAY = Duration.ofDays(1).toMillis()
        val MILLIS_PER_WEEK = Duration.ofDays(7).toMillis()
        val MILLIS_PER_MONTH = Duration.ofDays(31).toMillis()
    }

    private fun techProgression(
        targetLevel: TechnologyLevel,
        millisElapsed: Long? = null,
        condition: () -> Boolean,
    ): () -> Boolean {
        return {
            when {
                data.technologyLevel == targetLevel -> true
                (millisElapsed?.let { it < data.dateMillis - data.epochMillis } ?: true) && condition() -> {
                    data.technologyLevel = targetLevel
                    _technologyLevel.value = targetLevel
                    true
                }
                else -> false
            }
        }
    }

    // These need to be idempotent as not persisted.
    private val progressionStack = mutableListOf(
        techProgression(
            targetLevel = TechnologyLevel.Amateur,
            millisElapsed = MILLIS_PER_MONTH,
            condition = { _area.value.dimension >= 4 },
        ),
        techProgression(
            targetLevel = TechnologyLevel.Basic,
            condition = { _tool.value == Tool.Scythe },
        ),
        techProgression(
            targetLevel = TechnologyLevel.Intermediate,
            condition = { _technologies.contains(Technology.AutoHarvester) },
        ),
        techProgression(
            targetLevel = TechnologyLevel.Advanced,
            condition = { _technologies.contains(Technology.ScovilleDistillery) },
        ),
        techProgression(
            targetLevel = TechnologyLevel.Quantum,
            condition = { _technologies.contains(Technology.ChimoleonGenetics) },
        ),
    )

    private fun plantTypeProgression(
        plantType: PlantType,
        millisElapsed: Long? = null,
        condition: () -> Boolean,
    ): () -> Boolean {
        return {
            when {
                _plantTypes.contains(plantType) -> true
                (millisElapsed?.let { it < data.dateMillis - data.epochMillis } ?: true) && condition() -> {
                    _plantTypes.add(plantType)
                    true
                }
                else -> false
            }
        }
    }

    private val plantTypeStack = mutableListOf(
        plantTypeProgression(
            plantType = PlantType.Poblano,
            condition = { true },
        ),
        plantTypeProgression(
            plantType = PlantType.Guajillo,
            condition = { _light.value.ordinal >= Light.CFL.ordinal },
        ),
        plantTypeProgression(
            plantType = PlantType.Jalapeno,
            condition = { _area.value.ordinal >= Area.Bedroom.ordinal },
        ),
        plantTypeProgression(
            plantType = PlantType.BirdsEye,
            condition = { _area.value.ordinal >= Area.SpareRoom.ordinal },
        ),
    )

    private val _balance = MutableLiveData(data.balance)
    val balance: LiveData<Currency> by ::_balance

    private val _pepperInventory = data.pepperInventory.toMutableStateMap()
    val pepperInventory: SnapshotStateMap<PlantType, StockLevel> by ::_pepperInventory

    private val _distillateInventory = data.distillateInventory.toMutableStateMap()
    val distillateInventory: SnapshotStateMap<Distillate, FractionalStockLevel> by ::_distillateInventory

    private val _area = mutableStateOf(data.area)
    val area: State<Area> by ::_area

    private val _plantPots = data.plantPots.toMutableStateList()
    val plantPots: SnapshotStateList<PlantPot> by ::_plantPots

    private val _technologies = data.technologies.toMutableStateList()
    val technologies: SnapshotStateList<Technology> by ::_technologies

    private val _plantTypes = data.plantTypes.toMutableStateList()
    val plantTypes: SnapshotStateList<PlantType> by ::_plantTypes

    private val _light = mutableStateOf(data.light)
    val light: State<Light> by ::_light

    private val _medium = mutableStateOf(data.medium)
    val medium: State<Medium> by ::_medium

    private val _tool = mutableStateOf(data.tool)
    val tool: State<Tool> by ::_tool

    private val _technologyLevel = mutableStateOf(data.technologyLevel)
    val technologyLevel: State<TechnologyLevel> by ::_technologyLevel

    private val _autoHarvestEnabled = mutableStateOf(data.autoHarvestEnabled)
    val autoHarvestEnabled: State<Boolean> by ::_autoHarvestEnabled

    // Using LiveData to get around an issue at startup where we try and read a value
    // before it is snapshot.
    private val _dateMillis = MutableLiveData(data.dateMillis)
    val dateMillis: LiveData<Long> by ::_dateMillis

    private val _geneticComputationState = mutableStateOf(data.geneticComputationState)
    val geneticComputationState: State<GeneticComputationState> by ::_geneticComputationState

    var buyer = Membership.Friends
        private set

    val id: ObjectId
        get() = data.id

    fun harvestOrCompost(plantPot: PlantPot) {
        val millis = data.dateMillis

        plantPot.plant?.let {
            val isHarvesting = it.isRipe(millis)

            val allPots = getSurroundingMaturedPots(
                pot = plantPot,
                millis = millis,
                initialRipe = isHarvesting,
            )

            val pots = when (data.tool) {
                Tool.Scythe -> allPots
                Tool.None -> allPots.take(1)
            }

            for (pot in pots) {
                if (isHarvesting) {
                    _pepperInventory.compute(pot.plant!!.plantType) { _, stock ->
                        StockLevel(
                            quantity = pot.plant.harvest().plus(stock?.quantity ?: 0)
                        )
                    }
                }
                removePlant(plantPot = pot)
            }
        }
    }

    // Either surrounding ripe, or dead.
    private fun getSurroundingMaturedPots(
        pot: PlantPot,
        millis: Long,
        initialRipe: Boolean,
    ): Sequence<PlantPot> = sequence {
        val dimension = data.area.dimension
        pot.plant?.let {
            val isRipe = it.isRipe(millis) && initialRipe
            val isDead = it.isDead(millis) && !initialRipe

            // TODO: this does loads of unnecessary work.
            //      I should make it recurse in a direction, or something more elegant.
            if (isRipe || isDead) {
                // get index first, as we replace with a new immutable object
                val centerIndex = _plantPots.indexOf(pot)

                yield(pot)

                val col = centerIndex % dimension

                if (col > 0) {
                    // not on left edge
                    yieldAll(
                        getSurroundingMaturedPots(
                            pot = _plantPots[centerIndex - 1],
                            initialRipe = initialRipe,
                            millis = millis
                        )
                    )
                }

                if (col < dimension - 1) {
                    // not on right edge
                    yieldAll(
                        getSurroundingMaturedPots(
                            pot = _plantPots[centerIndex + 1],
                            initialRipe = initialRipe,
                            millis = millis
                        )
                    )
                }

                val row = centerIndex / dimension

                if (row > 0) {
                    // not on top edge
                    yieldAll(
                        getSurroundingMaturedPots(
                            pot = _plantPots[centerIndex - dimension],
                            initialRipe = initialRipe,
                            millis = millis
                        )
                    )
                }

                if (row < dimension - 1) {
                    // not on bottom edge
                    yieldAll(
                        getSurroundingMaturedPots(
                            pot = _plantPots[centerIndex + dimension],
                            initialRipe = initialRipe,
                            millis = millis
                        )
                    )
                }
            }
        }
    }

    private fun removePlant(plantPot: PlantPot) {
        _plantPots[_plantPots.indexOf(plantPot)] = PlantPot(plant = null)
    }

    fun sellPeppers(plantType: PlantType) {
        _pepperInventory[plantType]?.let {
            _pepperInventory[plantType] = StockLevel(quantity = 0)
            data.balance = data.balance.copy(
                total = data.balance.total
                    .plus(buyer.total(plantType = plantType, quantity = it.quantity))
            )
            _balance.postValue(data.balance)
        }
    }

    fun sellDistillate(distillate: Distillate) {
        _distillateInventory[distillate]?.let {
            _distillateInventory[distillate] = it.copy(quantity = 0)
            data.balance = data.balance.copy(
                total = data.balance.total
                    .plus(buyer.total(distillate = distillate, quantity = it.quantity))
            )
            _balance.postValue(data.balance)
        }
    }

    fun buyLightUpgrade(desiredLight: Light) {
        if (deductPurchaseCost(desiredLight)) {
            data.light = desiredLight
            _light.value = desiredLight
        }
    }

    fun buyMediumUpgrade(desiredMedium: Medium) {
        if (deductPurchaseCost(desiredMedium)) {
            data.medium = desiredMedium
            _medium.value = desiredMedium
        }
    }

    fun buyToolUpgrade(desiredTool: Tool) {
        if (deductPurchaseCost(desiredTool)) {
            data.tool = desiredTool
            _tool.value = desiredTool
        }
    }

    fun buyTechnology(desiredTechnology: Technology) {
        if (deductPurchaseCost(desiredTechnology)) {
            _technologies.add(desiredTechnology)
            if (desiredTechnology == Technology.AutoHarvester) {
                toggleAutoHarvesting()
            }
        }
    }

    fun buyAreaUpgrade(desiredArea: Area) {
        if (deductPurchaseCost(desiredArea)) {
            _plantPots.addAll(
                List(desiredArea.total - data.area.total) { PlantPot(plant = null) }
            )
            data.area = desiredArea
            _area.value = desiredArea
        }
    }

    private fun deductPurchaseCost(upgrade: Purchasable): Boolean {
        val cost = upgrade.cost!!.total
        if (data.balance.total >= cost) {
            data.balance = data.balance.copy(
                total = data.balance.total - cost
            )
            return true
        }

        return false
    }

    fun autoPlantChecked(plantType: PlantType, checked: Boolean) {
        if (technologies.contains(Technology.AutoPlanter)) {
            for (i in 0 until _plantTypes.size) {
                val p = _plantTypes[i]
                if (p == plantType) {
                    _plantTypes[i] = p.copy(autoPlantChecked = checked)
                } else if (checked && p.autoPlantChecked) {
                    _plantTypes[i] = p.copy(autoPlantChecked = false)
                }
            }
        }
    }

    // Have to ensure the UI blocks changing this while isActive
    fun setLeftGeneticsPlantType(plantType: PlantType) {
        _geneticComputationState.value = _geneticComputationState.value.copy(
            leftPlantType = plantType
        )
    }
    // Have to ensure the UI blocks changing this while isActive
    fun setRightGeneticsPlantType(plantType: PlantType) {
        _geneticComputationState.value = _geneticComputationState.value.copy(
            rightPlantType = plantType
        )
    }

    fun plantSeed(seed: Seed) {
        val index = _plantPots.indexOfFirst { it.plant == null }
        if (index >= 0 && deductPurchaseCost(seed.plantType)) {
            _plantPots[index] = PlantPot(
                plant = Plant(
                    plantType = seed.plantType,
                    epochMillis = data.dateMillis,
                    lightStrength = data.light.strength,
                    mediumEffectiveness = data.medium.effectiveness,
                )
            )
        }
    }

    fun distill(distillate: Distillate) {
        val totalScovilles = _pepperInventory.entries
            .sumOf { it.key.scovilles.count * it.value.quantity * it.key.size }

        if (totalScovilles < distillate.requiredScovilles.count) {
            return
        }

        var requiredScovilles = distillate.requiredScovilles.count

        loop@for (plantType in _pepperInventory.keys) {
            val stock = _pepperInventory[plantType]!!
            for (consumed in 1..stock.quantity) {
                requiredScovilles -= (plantType.scovilles.count * plantType.size)
                if (requiredScovilles <= 0) {
                    _pepperInventory[plantType] = StockLevel(
                        quantity = stock.quantity - consumed
                    )
                    break@loop;
                }
            }
            _pepperInventory[plantType] = StockLevel(quantity = 0)
        }

        _distillateInventory.compute(distillate) { _, stock ->
            stock?.copy(quantity = stock.quantity + 1)
                ?: FractionalStockLevel(quantity = 1, thousandths = 0)
        }
    }

    fun toggleAutoHarvesting() {
        val newState = !data.autoHarvestEnabled
        data.autoHarvestEnabled = newState
        _autoHarvestEnabled.value = newState
    }

    fun toggleComputation() {
        val newState = !_geneticComputationState.value.isActive
        _geneticComputationState.value = _geneticComputationState.value.copy(
            isActive = newState,
            wasStarted = _geneticComputationState.value.wasStarted || newState,
        )
    }

    fun resetComputation() {
        _geneticComputationState.value = GeneticComputationState.default().copy(
            leftPlantType = _geneticComputationState.value.leftPlantType,
            rightPlantType = _geneticComputationState.value.rightPlantType,
        )
    }

    fun updateFitnessSliders(trait: GeneticTrait, value: Float) {
        _geneticComputationState.value = _geneticComputationState.value.copy(
            fitnessFunctionData = _geneticComputationState.value.fitnessFunctionData.setValue(
                trait = trait,
                newValue = value,
            )
        )
    }

    private fun calculateCosts(): Long {
        val light = data.light
        val medium = data.medium

        return (light.joulesPerCostTick.toLong() * Costs.ElectricityJoule.cost)
         .plus(medium.litresPerCostTick * Costs.WaterLitre.cost)
         .times(
             _plantPots.count { it.plant?.isGrowing(data.dateMillis) == true }
         )
    }

    private fun onGeneticsComplete(newPlantType: PlantType) {
        val existing = _plantTypes.firstOrNull { it.displayName == newPlantType.displayName }

        if (existing != null) {
            // drop it
            // TODO: maybe compare fitness
        } else {
            _plantTypes.add(newPlantType)
        }

        _geneticComputationState.value = GeneticComputationState.default()
    }

    private fun runGenetics() {
        val nextGeneration = _geneticComputationState.value.tickGenerations(n = 1)
        if (nextGeneration.progress() == 100f) {
            onGeneticsComplete(nextGeneration.final())
        } else {
            _geneticComputationState.value = nextGeneration
        }
    }

    private fun maybeRunGenetics() {
        if (!_geneticComputationState.value.isActive) return

        val qcapCostThousandths = 100

        val quantumCaps = _distillateInventory.getOrDefault(
            Distillate.QuantumCapsicum,
            FractionalStockLevel(quantity = 0, thousandths = 0),
        )

        when {
            quantumCaps.thousandths > qcapCostThousandths -> {
                _distillateInventory[Distillate.QuantumCapsicum] = quantumCaps.copy(
                    thousandths = quantumCaps.thousandths - qcapCostThousandths
                )
                runGenetics()
            }
            quantumCaps.thousandths == qcapCostThousandths && quantumCaps.quantity > 0 -> {
                _distillateInventory[Distillate.QuantumCapsicum] = quantumCaps.copy(
                    quantity = quantumCaps.quantity - 1,
                    thousandths = 1000,
                )
                runGenetics()
            }
            quantumCaps.quantity > 0 -> {
                _distillateInventory[Distillate.QuantumCapsicum] = quantumCaps.copy(
                    quantity = quantumCaps.quantity - 1,
                    thousandths = quantumCaps.thousandths + 1000 - qcapCostThousandths,
                )
                runGenetics()
            }
            else -> {
                _geneticComputationState.value = _geneticComputationState.value.copy(
                    isActive = false
                )
            }
        }
    }

    fun onTick(): Boolean {
        val tickSize = if (_technologies.contains(Technology.TemporalDistortionField))
            MILLIS_PER_TICK * 4
        else
            MILLIS_PER_TICK

        data.dateMillis += tickSize
        _dateMillis.postValue(data.dateMillis)

        data.milliCounter += tickSize

        if (data.milliCounter >= MILLIS_PER_DAY) {
            data.milliCounter -= MILLIS_PER_DAY

            calculateCosts().also {
                if (it > data.balance.total) {
                    return true
                }

                data.balance = data.balance.copy(
                    total = data.balance.total - it
                )

                _balance.postValue(data.balance)
            }

            technologies
                .filter { it == Technology.AutoPlanter }
                .forEach { _ ->
                    val autoPlant = _plantTypes.firstOrNull { it.autoPlantChecked }
                    if (autoPlant != null) {
                        plantSeed(autoPlant.toSeed())
                    }
                }

            if (technologies.contains(Technology.AutoHarvester)
                && data.autoHarvestEnabled) {
                for (i in 0 until _plantPots.size) {
                    val pot = _plantPots[i]
                    if (pot.plant?.isRipe(data.dateMillis) == true
                        || pot.plant?.isDead(data.dateMillis) == true) {
                        harvestOrCompost(pot)
                    }
                }
            }

            progressionStack.tryPop()
            plantTypeStack.tryPop()

            maybeRunGenetics()
        }

        return false
    }

    fun snapshot() = data.copy(
        plantPots = _plantPots.toList(),
        pepperInventory = _pepperInventory.toList(),
        distillateInventory = _distillateInventory.toList(),
        technologies = _technologies.toList(),
        plantTypes = _plantTypes.toList(),
        geneticComputationState = _geneticComputationState.value.snapshot(),
    )
}