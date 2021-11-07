package com.will118.scoville2000.engine

import Area
import Light
import Medium
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

sealed interface GameOperation

object Tick : GameOperation
object Save : GameOperation
data class SellProduce(val plantType: PlantType) : GameOperation
data class Harvest(val plantPot: PlantPot) : GameOperation
data class Compost(val plantPot: PlantPot) : GameOperation
data class PlantSeed(val seed: Seed) : GameOperation
data class UpgradeLight(val light: Light) : GameOperation
data class UpgradeMedium(val medium: Medium) : GameOperation
data class UpgradeArea(val area: Area) : GameOperation

@ExperimentalCoroutinesApi
class GameStateExecutor(
    private val gameState: GameState,
    private val onSaveTick: suspend (GameStateData) -> Unit,
) {
    companion object {
        const val TICK_PERIOD = 16L
        const val SAVE_PERIOD = 1_000L
    }

    private val channel = Channel<GameOperation>()

    private suspend fun tickLoop() {
        while (!channel.isClosedForSend) {
            channel.send(Tick)
            delay(timeMillis = TICK_PERIOD)
        }
    }

    private suspend fun saveLoop() {
        while (!channel.isClosedForSend) {
            channel.send(Save)
            delay(timeMillis = SAVE_PERIOD)
        }
    }

    private suspend fun executorLoop(onGameOver: () -> Unit) {
        while (!channel.isClosedForReceive) {
            when (val operation = channel.receive()) {
                is Save -> onSaveTick(gameState.snapshot())
                is Tick -> {
                    if (gameState.onTick()) {
                        onGameOver()
                    }
                }
                is Harvest -> gameState.harvest(operation.plantPot)
                is Compost -> gameState.compost(operation.plantPot)
                is PlantSeed -> gameState.plantSeed(operation.seed)
                is SellProduce -> gameState.sellProduce(operation.plantType)
                is UpgradeLight -> gameState.buyLightUpgrade(operation.light)
                is UpgradeMedium -> gameState.buyMediumUpgrade(operation.medium)
                is UpgradeArea -> gameState.buyAreaUpgrade(operation.area)
            }
        }
    }

    suspend fun loop(onGameOver: () -> Unit) {
        coroutineScope {
            launch {
                executorLoop {
                    channel.close()
                    onGameOver()
                }
            }
            launch { saveLoop() }
            launch { tickLoop() }
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