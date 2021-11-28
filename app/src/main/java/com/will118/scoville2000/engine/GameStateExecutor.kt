package com.will118.scoville2000.engine

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

sealed interface GameOperation

object Tick : GameOperation
object Save : GameOperation
data class SellPeppers(val plantType: PlantType) : GameOperation
data class SetLeftGeneticCross(val plantType: PlantType) : GameOperation
data class SetRightGeneticCross(val plantType: PlantType) : GameOperation
data class SellDistillate(val distillate: Distillate) : GameOperation
object ToggleAutoHarvesting : GameOperation
object ToggleComputation : GameOperation
data class HarvestOrCompost(val plantPot: PlantPot) : GameOperation
data class AutoPlantChecked(val plantType: PlantType, val checked: Boolean) : GameOperation
data class PlantSeed(val seed: Seed) : GameOperation
data class UpgradeLight(val light: Light) : GameOperation
data class UpgradeMedium(val medium: Medium) : GameOperation
data class UpgradeArea(val area: Area) : GameOperation
data class UpgradeTool(val tool: Tool) : GameOperation
data class Distill(val distillate: Distillate) : GameOperation
data class PurchaseTechnology(val technology: Technology) : GameOperation
data class SetFitnessSlider(val trait: GeneticTrait, val newValue: Float) : GameOperation

@ExperimentalCoroutinesApi
class GameStateExecutor(
    private val gameState: GameState,
    private val onSaveTick: suspend (GameStateData) -> Unit,
) {
    companion object {
        const val TICK_PERIOD_MS = 250L
        const val SAVE_PERIOD_MS = 10_000L
    }

    private val channel = Channel<GameOperation>(Channel.UNLIMITED)

    private suspend fun executorLoop(): Boolean {
        when (val operation = channel.receive()) {
            is Save -> {
                val snapshot = gameState.snapshot()
                println("save: ${snapshot.hashCode()}")
                onSaveTick(snapshot)
            }
            is Tick -> {
                if (gameState.onTick()) {
                    return false
                }
            }
            is HarvestOrCompost -> gameState.harvestOrCompost(operation.plantPot)
            is PlantSeed -> gameState.plantSeed(operation.seed)
            is SellPeppers -> gameState.sellPeppers(operation.plantType)
            is SellDistillate -> gameState.sellDistillate(operation.distillate)
            is UpgradeLight -> gameState.buyLightUpgrade(operation.light)
            is UpgradeMedium -> gameState.buyMediumUpgrade(operation.medium)
            is UpgradeArea -> gameState.buyAreaUpgrade(operation.area)
            is UpgradeTool -> gameState.buyToolUpgrade(operation.tool)
            is PurchaseTechnology -> gameState.buyTechnology(operation.technology)
            is AutoPlantChecked -> gameState.autoPlantChecked(operation.plantType, operation.checked)
            is ToggleAutoHarvesting -> gameState.toggleAutoHarvesting()
            is Distill -> gameState.distill(operation.distillate)
            is SetLeftGeneticCross -> gameState.setLeftGeneticsPlantType(operation.plantType)
            is SetRightGeneticCross -> gameState.setRightGeneticsPlantType(operation.plantType)
            is SetFitnessSlider -> gameState.updateFitnessSliders(operation.trait, operation.newValue)
            is ToggleComputation -> gameState.toggleComputation()
        }

        return true
    }

    private val timer = Timer().also {
        it.scheduleAtFixedRate(delay = 0, period = TICK_PERIOD_MS) {
            runBlocking {
                channel.send(Tick)
            }
        }

        it.scheduleAtFixedRate(delay = SAVE_PERIOD_MS, period = SAVE_PERIOD_MS) {
            runBlocking {
                channel.send(Save)
            }
        }
    }

    suspend fun loop(onGameOver: () -> Unit) = coroutineScope {
        while (isActive && !channel.isClosedForReceive) {
            if (!executorLoop()) {
                timer.cancel()
                channel.close()
                onGameOver()
            }
        }
    }

    fun enqueueSync(operation: GameOperation) {
        runBlocking {
            if (!channel.isClosedForSend) {
                channel.send(operation)
            }
        }
    }
}