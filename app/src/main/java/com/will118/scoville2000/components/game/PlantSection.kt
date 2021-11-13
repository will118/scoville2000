package com.will118.scoville2000.components.game

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.unit.dp
import com.will118.scoville2000.engine.Area
import com.will118.scoville2000.engine.PlantPot
import com.will118.scoville2000.engine.Technology
import com.will118.scoville2000.ui.theme.Typography

@ExperimentalGraphicsApi
@Composable
fun PlantSection(
    technologies: SnapshotStateList<Technology>,
    plantPots: SnapshotStateList<PlantPot>,
    autoHarvestEnabled: Boolean,
    area: Area,
    dateMillis: Long,
    onPlantPotTap: (PlantPot) -> Unit,
    toggleAutoHarvesting: () -> Unit,
) {
    val autoHarvest = technologies.contains(Technology.AutoHarvester)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.height(35.dp)) {
            Text(
                text = "Plants",
                style = Typography.h5
            )

            if (autoHarvest) {
                Spacer(modifier = Modifier.weight(1.0f))

                Button(
                    onClick = { toggleAutoHarvesting() },
                    modifier = Modifier.height(35.dp),
                ) {
                    Text(if (autoHarvestEnabled) "AutoHarvest ON" else "AutoHarvest OFF")
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "${plantPots.count { it.plant != null }}/${area.total} (${area.displayName})",
            style = Typography.subtitle2
        )
        Spacer(modifier = Modifier.height(10.dp))
        PlantGrid(
            area = area,
            plantPots = plantPots,
            dateMillis = dateMillis,
            onPlantPotTap = onPlantPotTap,
        )
    }
}
