package com.will118.scoville2000.components.game

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.unit.dp
import com.will118.scoville2000.engine.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalGraphicsApi
@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
@Composable
fun Game(
    area: Area,
    balance: Currency,
    buyer: Buyer,
    dateMillis: Long,
    light: Light,
    medium: Medium,
    tool: Tool,
    technologyLevel: TechnologyLevel,
    plantPots: SnapshotStateList<PlantPot>,
    inventory: SnapshotStateMap<PlantType, StockLevel>,
    technologies: SnapshotStateList<Technology>,
    onPlantPotTap: (PlantPot) -> Unit,
    sell: (PlantType) -> Unit,
    upgradeArea: (Area) -> Unit,
    upgradeMedium: (Medium) -> Unit,
    upgradeLight: (Light) -> Unit,
    upgradeTool: (Tool) -> Unit,
    plantSeed: (Seed) -> Unit,
    purchaseTechnology: (Technology) -> Unit,
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
            plantPots = plantPots,
            area = area,
            dateMillis = dateMillis,
            onPlantPotTap = onPlantPotTap,
        )
        Divider(modifier = dividerPadding)
        InventorySection(
            inventory = inventory,
            sell = sell,
        )
        Divider(modifier = dividerPadding)
        StatSection(
            balance = balance,
            dateMillis = dateMillis,
            light = light,
            medium = medium,
            buyer = buyer,
        )
        Divider(modifier = dividerPadding)
        ShopSection(
            currentLight = light,
            currentArea = area,
            currentMedium = medium,
            currentTool = tool,
            currentTechnologies = technologies,
            plantSeed = plantSeed,
            upgradeLight = upgradeLight,
            upgradeMedium = upgradeMedium,
            upgradeArea = upgradeArea,
            upgradeTool = upgradeTool,
        )
        if (technologyLevel != TechnologyLevel.None) {
            Divider(modifier = dividerPadding)
            TechSection(
                technologyLevel = technologyLevel,
                purchaseTechnology = purchaseTechnology,
            )
        }
    }
}