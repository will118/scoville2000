package com.will118.scoville2000

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import com.will118.scoville2000.components.game.Game
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
        distillateInventory = gameState.distillateInventory,
        pepperInventory = gameState.pepperInventory,
        tool = gameState.tool.value,
        technologyLevel = gameState.technologyLevel.value,
        technologies = gameState.technologies,
        plantTypes = gameState.plantTypes,
        autoHarvestEnabled = gameState.autoHarvestEnabled.value,
        autoPlantChecked = { plantType, checked ->
            gameStateExecutor.enqueueSync(AutoPlantChecked(plantType, checked))
        },
        onPlantPotTap = { gameStateExecutor.enqueueSync(HarvestOrCompost(it)) },
        distill = { gameStateExecutor.enqueueSync(Distill(it)) },
        sellDistillate = { gameStateExecutor.enqueueSync(SellDistillate(it)) },
        sellPeppers = { gameStateExecutor.enqueueSync(SellPeppers(it)) },
        plantSeed = { gameStateExecutor.enqueueSync(PlantSeed(it)) },
        upgradeLight = { gameStateExecutor.enqueueSync(UpgradeLight(it)) },
        upgradeMedium = { gameStateExecutor.enqueueSync(UpgradeMedium(it)) },
        upgradeArea = { gameStateExecutor.enqueueSync(UpgradeArea(it)) },
        upgradeTool = { gameStateExecutor.enqueueSync(UpgradeTool(it)) },
        purchaseTechnology = { gameStateExecutor.enqueueSync(PurchaseTechnology(it)) },
        toggleAutoHarvesting = { gameStateExecutor.enqueueSync(ToggleAutoHarvesting) }
    )
}
