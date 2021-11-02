package com.will118.gt2000.engine

import Area
import Light
import Medium
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.thread

sealed interface GameOperation

object Tick : GameOperation
data class SellProduce(val plantType: PlantType) : GameOperation
data class Harvest(val plant: Plant) : GameOperation
data class Compost(val plant: Plant) : GameOperation
data class PlantSeed(val seed: Seed) : GameOperation
data class UpgradeLight(val light: Light) : GameOperation
data class UpgradeMedium(val medium: Medium) : GameOperation
data class UpgradeArea(val area: Area) : GameOperation

@ExperimentalCoroutinesApi
class GameStateExecutor(private val gameState: GameState, private val onGameOver: () -> Unit) {
    companion object {
        const val PERIOD = 16L
    }

    private val channel = Channel<GameOperation>()

    private val timerTask = Timer().schedule(delay = 0, period = PERIOD) {
        runBlocking {
            enqueue(Tick)
        }
    }

    private val executorThread = thread {
        runBlocking {
            while (!channel.isClosedForReceive) {
                when (val operation = channel.receive()) {
                    is Tick -> {
                        if (gameState.onTick()) {
                            gameOver()
                        }
                    }
                    is Harvest -> gameState.harvest(operation.plant)
                    is Compost -> gameState.compost(operation.plant)
                    is PlantSeed -> gameState.plantSeed(operation.seed)
                    is SellProduce -> gameState.sellProduce(operation.plantType)
                    is UpgradeLight -> gameState.buyLightUpgrade(operation.light)
                    is UpgradeMedium -> gameState.buyMediumUpgrade(operation.medium)
                    is UpgradeArea -> gameState.buyAreaUpgrade(operation.area)
                }
            }
        }
    }

    private fun gameOver() {
        timerTask.cancel()
        channel.close()
        onGameOver()
    }

    fun enqueue(operation: GameOperation) {
        runBlocking {
            if (!channel.isClosedForSend) {
                channel.send(operation)
            }
        }
    }
}