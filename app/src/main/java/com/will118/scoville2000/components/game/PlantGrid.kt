package com.will118.scoville2000.components.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import com.will118.scoville2000.engine.Area
import com.will118.scoville2000.engine.PhaseNames
import com.will118.scoville2000.engine.PlantPot
import kotlin.math.floor

private fun getTappedPlant(
    offset: Offset,
    size: IntSize,
    area: Area,
    plantPots: List<PlantPot>
): PlantPot? {
    val lines = Integer.max(1, area.dimension)
    val step = size.height.toFloat() / lines
    val index = floor(offset.x / step).toInt() + (floor(offset.y / step).toInt() * area.dimension)
    return plantPots.getOrNull(index)
}

@ExperimentalGraphicsApi
@Composable
fun PlantGrid(
    area: Area,
    plantPots: SnapshotStateList<PlantPot>,
    dateMillis: Long,
    onPlantPotTap: (PlantPot) -> Unit,
) {
    val areaWrapper = rememberUpdatedState(newValue = area)
    val borderColor = MaterialTheme.colors.primary
    // cf. Mondrian
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .pointerInput("foo") {
            detectTapGestures { offset ->
                getTappedPlant(
                    offset = offset,
                    size = this.size,
                    area = areaWrapper.value,
                    plantPots = plantPots,
                )?.let {
                    onPlantPotTap(it)
                }
            }
        }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawLine(
            start = Offset(x = 0f, y = 0f),
            end = Offset(x = 0f, y = canvasHeight),
            color = borderColor,
            strokeWidth = 5.5f
        )

        drawLine(
            start = Offset(x = 0f, y = canvasHeight),
            end = Offset(x = canvasWidth, y = canvasHeight),
            color = borderColor,
            strokeWidth = 5.5f
        )

        drawLine(
            start = Offset(x = canvasWidth, y = canvasHeight),
            end = Offset(x = canvasWidth, y = 0f),
            color = borderColor,
            strokeWidth = 5.5f
        )

        drawLine(
            start = Offset(x = canvasWidth, y = 0f),
            end = Offset(x = 0f, y = 0f),
            color = borderColor,
            strokeWidth = 5.5f
        )

        val lines = Integer.max(1, area.dimension)
        val step = canvasHeight / lines
//        val gridStrokeWidth = if (lines >= 32) 1f else 4f

//        for (i in (1 until lines)) {
//            val offset = step * i
//            drawLine(
//                start = Offset(x = 0f, y = offset),
//                end = Offset(x = canvasWidth, y = offset),
//                color = Color.Black,
//                strokeWidth = gridStrokeWidth
//            )
//
//            drawLine(
//                start = Offset(x = offset, y = 0f),
//                end = Offset(x = offset, y = canvasHeight),
//                color = Color.Black,
//                strokeWidth = gridStrokeWidth
//            )
//        }

        for (plantPot in plantPots.withIndex().filter { it.value.plant != null }) {
            drawRect(
                color = when (plantPot.value.plant!!.currentPhase(dateMillis)) {
                    PhaseNames.Sprout -> Color.hsv(92f, 0.11f, 0.88f)
                    PhaseNames.Seedling -> Color.hsv(93f, 0.29f, 0.88f)
                    PhaseNames.Vegetative -> Color.hsv(93f, 0.60f, 0.78f)
                    PhaseNames.Budding -> Color.hsv(93f, 0.68f, 0.68f)
                    PhaseNames.Flowering -> Color.hsv(93f, 0.68f, 0.53f)
                    PhaseNames.Ripening -> Color.hsv(35f, 1.00f, 0.70f)
                    null -> Color.hsv(0f, 0.71f, 0.30f)
                },
                topLeft = Offset(
                    x = (plantPot.index % area.dimension) * step,
                    y = (plantPot.index / area.dimension) * step
                ),
                size = Size(step, step),
            )
        }
    }
}
