package com.will118.scoville2000.components.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.unit.dp
import com.will118.scoville2000.engine.Area
import com.will118.scoville2000.engine.PlantPot
import com.will118.scoville2000.ui.theme.Typography

@ExperimentalGraphicsApi
@Composable
fun PlantSection(
    plantPots: SnapshotStateList<PlantPot>,
    area: Area,
    dateMillis: Long,
    onPlantPotTap: (PlantPot) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Plants",
            style = Typography.h5
        )
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
