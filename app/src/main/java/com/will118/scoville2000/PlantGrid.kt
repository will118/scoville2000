package com.will118.scoville2000

import Area
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import com.will118.scoville2000.engine.PhaseNames
import com.will118.scoville2000.engine.Plant
import java.time.Instant
import kotlin.math.floor

private fun getTappedPlant(
    offset: Offset,
    size: IntSize,
    area: Area,
    plants: List<Plant?>
): Plant? {
    val lines = Integer.max(1, area.dimension)
    val step = size.height.toFloat() / lines
    val index = floor(offset.x / step).toInt() + (floor(offset.y / step).toInt() * area.dimension)
    return plants.getOrNull(index)
}

@ExperimentalGraphicsApi
@Composable
fun PlantGrid(
    area: State<Area>,
    plants: SnapshotStateList<Plant?>,
    date: State<Instant?>,
    harvest: (Plant) -> Unit,
    compost: (Plant) -> Unit,
) {
    // cf. Mondrian
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .pointerInput("foo") {
            detectTapGestures { offset ->
                getTappedPlant(
                    offset = offset,
                    size = this.size,
                    area = area.value,
                    plants = plants,
                )?.let {
                    val phase = it.currentPhase(date.value!!)

                    if (phase?.isRipe == true) {
                        harvest(it)
                    }
                    if (phase == null) {
                        compost(it)
                    }
                }
            }
        }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawLine(
            start = Offset(x = 0f, y = 0f),
            end = Offset(x = 0f, y = canvasHeight),
            color = Color.Blue,
            strokeWidth = 5.5f
        )

        drawLine(
            start = Offset(x = 0f, y = canvasHeight),
            end = Offset(x = canvasWidth, y = canvasHeight),
            color = Color.Blue,
            strokeWidth = 5.5f
        )

        drawLine(
            start = Offset(x = canvasWidth, y = canvasHeight),
            end = Offset(x = canvasWidth, y = 0f),
            color = Color.Blue,
            strokeWidth = 5.5f
        )

        drawLine(
            start = Offset(x = canvasWidth, y = 0f),
            end = Offset(x = 0f, y = 0f),
            color = Color.Blue,
            strokeWidth = 5.5f
        )

        val lines = Integer.max(1, area.value.dimension)
        val step = canvasHeight / lines

        for (i in (1 until lines + 1)) {
            val offset = step * i
            drawLine(
                start = Offset(x = 0f, y = offset),
                end = Offset(x = canvasWidth, y = offset),
                color = Color.Black,
                strokeWidth = 4f
            )

            drawLine(
                start = Offset(x = offset, y = 0f),
                end = Offset(x = offset, y = canvasHeight),
                color = Color.Black,
                strokeWidth = 4f
            )
        }

        for (plant in plants.withIndex().filter { it.value != null }) {
            drawRect(
                color = when (plant.value!!.currentPhase(date.value!!)) {
                    PhaseNames.Sprout -> Color.hsv(92f, 0.5f, 0.98f)
                    PhaseNames.Seedling -> Color.hsv(93f, 0.29f, 0.88f)
                    PhaseNames.Vegetative -> Color.hsv(93f, 0.60f, 0.78f)
                    PhaseNames.Budding -> Color.hsv(93f, 0.68f, 0.68f)
                    PhaseNames.Flowering -> Color.hsv(93f, 0.68f, 0.53f)
                    PhaseNames.Ripening -> Color.hsv(9f, 1.00f, 0.70f)
                    null -> Color.hsv(9f, 1.00f, 0.30f)
                },
                topLeft = Offset(
                    x = (plant.index % area.value.dimension) * step,
                    y = (plant.index / area.value.dimension) * step
                ),
                size = Size(step, step),
            )
        }
    }
}
