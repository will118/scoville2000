package com.will118.scoville2000.components.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.will118.scoville2000.engine.*
import com.will118.scoville2000.ui.theme.Typography

@Composable
private fun SeedTable(
    currentPlantTypes: SnapshotStateList<PlantType>,
    currentTechnologies: SnapshotStateList<Technology>,
    plantSeed: (Seed) -> Unit,
    autoPlantChecked: (PlantType, Boolean) -> Unit,
) {
    Text(text = "Seeds", style = Typography.subtitle2)
    Spacer(modifier = Modifier.height(5.dp))

    val hasAutoPlanter = currentTechnologies.contains(Technology.AutoPlanter)

    Table(
        columns = listOfNotNull(
            TableColumn(header = null),
            TableColumn(header = null),
            TableColumn(header = null),
            if (hasAutoPlanter) TableColumn(header = null, weight = 0.5f) else null,
        ),
        items = currentPlantTypes.asSequence(),
        renderItem = { column, item ->
            when (column.index) {
                0 -> TableCellText(text = item.displayName)
                1 -> TableCellText(text = item.cost!!.toString())
                2 -> TextButton(onClick = { plantSeed(item.toSeed()) }) {
                    Text(
                        text = "Plant",
                    )
                }
                3 -> {
                    IconToggleButton(
                        checked = item.autoPlantChecked,
                        onCheckedChange = { autoPlantChecked(item, it) }
                    ) {
                        val tint by animateColorAsState(if (item.autoPlantChecked) Color(0xFFFF9800) else Color(0xFFB0BEC5))
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Auto plant",
                            tint = tint,
                        )
                    }
                }
            }
        },
    )

    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun ShopSection(
    currentLight: Light,
    currentArea: Area,
    currentMedium: Medium,
    currentTool: Tool,
    currentTechnologies: SnapshotStateList<Technology>,
    currentPlantTypes: SnapshotStateList<PlantType>,
    autoPlantChecked: (PlantType, Boolean) -> Unit,
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
        SeedTable(
            currentPlantTypes = currentPlantTypes,
            currentTechnologies = currentTechnologies,
            plantSeed = plantSeed,
            autoPlantChecked = autoPlantChecked,
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