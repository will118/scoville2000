package com.will118.scoville2000.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.will118.scoville2000.engine.GameStateExecutor
import com.will118.scoville2000.engine.ObjectId
import com.will118.scoville2000.engine.PlantType
import com.will118.scoville2000.engine.Seed
import com.will118.scoville2000.ui.theme.Typography
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun ChilliDex(
    objectStateId: ObjectId,
    gameStateExecutor: GameStateExecutor,
    currentPlantTypes: SnapshotStateList<PlantType>,
    plantSeed: (Seed) -> Unit,
    autoPlantTechnologyCapable: Boolean,
    autoPlantChecked: (PlantType, Boolean) -> Unit,
    onGameOver: () -> Unit,
) {
    LaunchedEffect(key1 = objectStateId, block = {
        gameStateExecutor.loop(onGameOver)
    })

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            color = Color.LightGray,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ChilliDEX",
                    modifier = Modifier.padding(
                        start = 15.dp,
                        top = 10.dp,
                        bottom = 10.dp,
                    ),
                    style = Typography.h5,
                )
                Spacer(modifier = Modifier.weight(1.0f))
                Text(
                    text = "${currentPlantTypes.size}/${PlantType.TOTAL_PEPPER_TYPES}",
                    modifier = Modifier.padding(
                        start = 15.dp,
                        top = 10.dp,
                        bottom = 10.dp,
                        end = 15.dp,
                    ),
                    style = Typography.h5.merge(SpanStyle(color = Color.Gray)),
                )
            }
        }

       val plantsWithEmpties = currentPlantTypes
           .plus(sequence<PlantType?> { while (true) yield(null) }
               .take(PlantType.TOTAL_PEPPER_TYPES - currentPlantTypes.size)
           )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(plantsWithEmpties) { plantType ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                        .clickable { },
                    elevation = 10.dp
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.padding(start = 15.dp, top = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (plantType == null) {
                                Text(
                                    text = "???",
                                    style = Typography.h6.merge(SpanStyle(color = Color.LightGray)),
                                )
                            } else {
                                Text(
                                    text = "${plantType.displayName}",
                                    style = Typography.h6,
                                )

                                if (autoPlantTechnologyCapable) {
                                    Spacer(modifier = Modifier.weight(1.0f))
                                    IconToggleButton(
                                        checked = plantType.autoPlantChecked,
                                        onCheckedChange = { autoPlantChecked(plantType, it) }
                                    ) {
                                        val tint by animateColorAsState(
                                            if (plantType.autoPlantChecked)
                                                Color(0xFFFF9800)
                                            else
                                                Color(0xFFB0BEC5)
                                        )
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = "Auto plant",
                                            tint = tint,
                                        )
                                    }
                                }
                            }
                        }

                        Row {
                            Column(
                                modifier = Modifier
                                    .padding(15.dp)
                                    .weight(1.0f)
                            ) {
                                if (plantType != null) {
                                    Spacer(modifier = Modifier.height(5.dp))
                                    StatText(
                                        name = "Lineage",
                                        value = plantType.lineage?.let {
                                            "${it.first.displayName} x ${it.second.displayName}"
                                        } ?: "Unknown",
                                        size = 12.sp,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    StatText(
                                        name = "Scovilles",
                                        value = plantType.scovilles.toString(),
                                        size = 12.sp,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    StatText(
                                        name = "Yield",
                                        value = "${plantType.yield}",
                                        size = 12.sp,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    StatText(
                                        name = "Size",
                                        value = "${plantType.size}",
                                        size = 12.sp,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    StatText(
                                        name = "Growth Duration",
                                        value = "${plantType.phases.totalDuration}",
                                        size = 12.sp,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    StatText(
                                        name = "Cost",
                                        value = plantType.cost.toString(),
                                        size = 12.sp,
                                    )
                                }
                            }

                            Box(modifier = Modifier
                                .height(160.dp)
                                .padding(bottom = 10.dp, end = 10.dp)
                                .align(Alignment.CenterVertically)
                                .aspectRatio(1.0f)) {
                                plantType.renderIcon()
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun renderUnknownIcon() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            color = Color.LightGray,
            topLeft = Offset(0f, 0f),
            size = Size(
                width = this.size.width,
                height = this.size.height,
            )
        )
    }
}

@Composable
fun renderPlantIcon() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            color = Color.LightGray,
            topLeft = Offset(0f, 0f),
            size = Size(
                width = this.size.width,
                height = this.size.height,
            )
        )
    }
}

@Composable
private fun PlantType?.renderIcon() {
    if (this == null) {
        renderUnknownIcon()
    } else {
        renderPlantIcon()
    }
}
