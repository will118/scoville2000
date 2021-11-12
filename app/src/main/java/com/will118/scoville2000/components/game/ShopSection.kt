package com.will118.scoville2000.components.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.will118.scoville2000.engine.*
import com.will118.scoville2000.ui.theme.Typography

@Composable
fun ShopSection(
    currentLight: Light,
    currentArea: Area,
    currentMedium: Medium,
    currentTool: Tool,
    currentTechnologies: SnapshotStateList<Technology>,
    plantSeed: (Seed) -> Unit,
    upgradeLight: (Light) -> Unit,
    upgradeMedium: (Medium) -> Unit,
    upgradeArea: (Area) -> Unit,
    upgradeTool: (Tool) -> Unit,
) {
    @Composable
    fun <T> product(
        header: String,
        products: List<T>,
        buttonText: String,
        onClick: (T) -> Unit,
    ) where T : Describe, T : Purchasable {
        if (products.isNotEmpty()) {
            Text(text = header, style = Typography.subtitle2)
            Spacer(modifier = Modifier.height(5.dp))
            ShopTable(
                items = products.asSequence()
            ) {
                TextButton(onClick = { onClick(it) }) {
                    Text(
                        text = buttonText,
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    Column {
        Text(text = "Shop", style = Typography.h5)
        Spacer(modifier = Modifier.height(10.dp))

        product(
            header = "Seeds",
            products = PlantType.values().filter { it.cost != null },
            buttonText = "Plant",
            onClick = { plantSeed(it.toSeed()) },
        )
        product(
            header = "Lights",
            products = currentLight.upgrades,
            buttonText = "Upgrade",
            onClick = upgradeLight,
        )
        product(
            header = "Growth medium",
            products = currentMedium.upgrades,
            buttonText = "Upgrade",
            onClick = upgradeMedium,
        )
        product(
            header = "Area",
            products = currentArea.upgrades,
            buttonText = "Upgrade",
            onClick = upgradeArea,
        )
        product(
            header = "Tools",
            products = Tool.values().dropWhile { it.ordinal <= currentTool.ordinal },
            buttonText = "Upgrade",
            onClick = upgradeTool,
        )
    }
}