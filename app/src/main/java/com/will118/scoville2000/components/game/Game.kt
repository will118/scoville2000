package com.will118.scoville2000.components.game

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.unit.dp
import com.will118.scoville2000.engine.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalComposeUiApi
@ExperimentalGraphicsApi
@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
@Composable
fun Game(
    area: Area,
    balance: Currency,
    membership: Membership,
    dateMillis: Long,
    light: Light,
    medium: Medium,
    tool: Tool,
    technologyLevel: TechnologyLevel,
    plantPots: SnapshotStateList<PlantPot>,
    distillateInventory: SnapshotStateMap<Distillate, FractionalStockLevel>,
    pepperInventory: SnapshotStateMap<PlantType, StockLevel>,
    technologies: SnapshotStateList<Technology>,
    plantTypes: SnapshotStateList<PlantType>,
    geneticComputationState: GeneticComputationState,
    autoPlantChecked: (PlantType, Boolean) -> Unit,
    navigateToChilliDex: () -> Unit,
    onPlantPotTap: (PlantPot) -> Unit,
    sellDistillate: (Distillate) -> Unit,
    sellPeppers: (PlantType) -> Unit,
    setLeftGeneticsPlantType: (PlantType) -> Unit,
    setRightGeneticsPlantType: (PlantType) -> Unit,
    distill: (Distillate) -> Unit,
    upgradeArea: (Area) -> Unit,
    upgradeMedium: (Medium) -> Unit,
    upgradeLight: (Light) -> Unit,
    upgradeTool: (Tool) -> Unit,
    updateFitnessSlider: (GeneticTrait, Float) -> Unit,
    plantSeed: (Seed) -> Unit,
    purchaseTechnology: (Technology) -> Unit,
    autoHarvestEnabled: Boolean,
    toggleAutoHarvesting: () -> Unit,
    toggleComputation: () -> Unit,
    resetComputation: () -> Unit,
) {
    val dividerPadding = Modifier.padding(vertical = 15.dp)

    Column(modifier = Modifier
        .padding(10.dp)
        .fillMaxWidth()
        .verticalScroll(
            state = rememberScrollState(),
            enabled = true
        )) {
        PlantSection(
            technologies = technologies,
            plantPots = plantPots,
            area = area,
            dateMillis = dateMillis,
            onPlantPotTap = onPlantPotTap,
            autoHarvestEnabled = autoHarvestEnabled,
            toggleAutoHarvesting = toggleAutoHarvesting,
        )
        Divider(modifier = dividerPadding)
        InventorySection(
            distillateInventory = distillateInventory,
            pepperInventory = pepperInventory,
            sellDistillate = sellDistillate,
            sellPeppers = sellPeppers,
        )
        Divider(modifier = dividerPadding)
        InfoSection(
            balance = balance,
            dateMillis = dateMillis,
            light = light,
            medium = medium,
            membership = membership,
            technologies = technologies,
        )
        Divider(modifier = dividerPadding)
        ShopSection(
            currentLight = light,
            currentArea = area,
            currentMedium = medium,
            currentTool = tool,
            currentTechnologies = technologies,
            currentPlantTypes = plantTypes,
            navigateToChilliDex = navigateToChilliDex,
            plantSeed = plantSeed,
            autoPlantChecked = autoPlantChecked,
            upgradeLight = upgradeLight,
            upgradeMedium = upgradeMedium,
            upgradeArea = upgradeArea,
            upgradeTool = upgradeTool,
        )
        if (technologyLevel != TechnologyLevel.None) {
            val visibleTechs = technologyLevel
                .visibleTechnologies()
                .filter { it.repeatablyPurchasable || !technologies.contains(it) }

            if (visibleTechs.isNotEmpty()) {
                Divider(modifier = dividerPadding)
                TechSection(
                    visibleTechnologies = visibleTechs,
                    purchaseTechnology = purchaseTechnology,
                )
            }
        }
        if (technologies.contains(Technology.ScovilleDistillery)) {
            Divider(modifier = dividerPadding)
            DistillerySection(
                distill = distill,
            )
        }
        if (technologies.contains(Technology.ChimoleonGenetics)) {
            Divider(modifier = dividerPadding)
            GeneticsSection(
                geneticComputationState = geneticComputationState,
                setLeftPlantType = setLeftGeneticsPlantType,
                setRightPlantType = setRightGeneticsPlantType,
                distillateInventory = distillateInventory,
                plantTypes = plantTypes,
                updateFitnessSlider = updateFitnessSlider,
                toggleComputation = toggleComputation,
                resetComputation = resetComputation,
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
    }
}