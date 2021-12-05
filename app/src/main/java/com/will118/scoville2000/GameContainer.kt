package com.will118.scoville2000

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import com.will118.scoville2000.components.game.Game
import com.will118.scoville2000.engine.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
@ExperimentalGraphicsApi
@Composable
fun GameContainer(
    gameState: GameState,
    gameStateExecutor: GameStateExecutor,
    navigateToChilliDex: () -> Unit,
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
        membership = gameState.membership.value,
        dateMillis = dateMillis.value,
        light = gameState.light.value,
        medium = gameState.medium.value,
        plantPots = gameState.plantPots,
        distillates = gameState.distillates,
        distillateInventory = gameState.distillateInventory,
        pepperInventory = gameState.pepperInventory,
        tool = gameState.tool.value,
        technologyLevel = gameState.technologyLevel.value,
        technologies = gameState.technologies,
        plantTypes = gameState.plantTypes,
        geneticComputationState = gameState.geneticComputationState.value,
        autoHarvestEnabled = gameState.autoHarvestEnabled.value,
        autoPlantChecked = { plantType, checked ->
            gameStateExecutor.enqueueSync(AutoPlantChecked(plantType, checked))
        },
        navigateToChilliDex = navigateToChilliDex,
        onPlantPotTap = { gameStateExecutor.enqueueSync(HarvestOrCompost(it)) },
        distill = { gameStateExecutor.enqueueSync(Distill(it)) },
        sellDistillate = { gameStateExecutor.enqueueSync(SellDistillate(it)) },
        sellPeppers = { gameStateExecutor.enqueueSync(SellPeppers(it)) },
        setLeftGeneticsPlantType = { gameStateExecutor.enqueueSync(SetLeftGeneticCross(it)) },
        setRightGeneticsPlantType = { gameStateExecutor.enqueueSync(SetRightGeneticCross(it)) },
        plantSeed = { gameStateExecutor.enqueueSync(PlantSeed(it)) },
        upgradeLight = { gameStateExecutor.enqueueSync(UpgradeLight(it)) },
        upgradeMedium = { gameStateExecutor.enqueueSync(UpgradeMedium(it)) },
        upgradeArea = { gameStateExecutor.enqueueSync(UpgradeArea(it)) },
        upgradeTool = { gameStateExecutor.enqueueSync(UpgradeTool(it)) },
        updateFitnessSlider = { trait, value ->
            gameStateExecutor.enqueueSync(SetFitnessSlider(trait, value))
        },
        purchaseTechnology = { gameStateExecutor.enqueueSync(PurchaseTechnology(it)) },
        toggleAutoHarvesting = { gameStateExecutor.enqueueSync(ToggleAutoHarvesting) },
        toggleComputation = { gameStateExecutor.enqueueSync(ToggleComputation) },
        resetComputation = { gameStateExecutor.enqueueSync(ResetComputation) },
        upgradeMembership = { gameStateExecutor.enqueueSync(UpgradeMembership(it)) }
    )
}
