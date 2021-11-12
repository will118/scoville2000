package com.will118.scoville2000

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import com.will118.scoville2000.engine.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
@ExperimentalGraphicsApi
@Composable
fun GameContainer(
    gameState: GameState,
    gameStateExecutor: GameStateExecutor,
    onGameOver: () -> Unit,
) {
    LaunchedEffect(key1 = gameState.id, block = {
        gameStateExecutor.loop(onGameOver)
    })

    val dateMillis = gameState.dateMillis.observeAsState(0)
    val balance = gameState.balance.observeAsState(Currency(0))

    Game(
        area = gameState.area.value,
        balance = balance.value,
        buyer = gameState.buyer,
        dateMillis = dateMillis.value,
        light = gameState.light.value,
        medium = gameState.medium.value,
        plantPots = gameState.plantPots,
        inventory = gameState.inventory,
        tool = gameState.tool.value,
        technologyLevel = gameState.technologyLevel.value,
        onPlantPotTap = { gameStateExecutor.enqueueSync(HarvestOrCompost(it)) },
        sell = { gameStateExecutor.enqueueSync(SellProduce(it)) },
        plantSeed = { gameStateExecutor.enqueueSync(PlantSeed(it)) },
        upgradeLight = { gameStateExecutor.enqueueSync(UpgradeLight(it)) },
        upgradeMedium = { gameStateExecutor.enqueueSync(UpgradeMedium(it)) },
        upgradeArea = { gameStateExecutor.enqueueSync(UpgradeArea(it)) },
        upgradeTool = { gameStateExecutor.enqueueSync(UpgradeTool(it)) },
    )
}
