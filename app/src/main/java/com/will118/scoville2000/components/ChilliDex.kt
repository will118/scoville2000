package com.will118.scoville2000.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.will118.scoville2000.engine.GameId
import com.will118.scoville2000.engine.GameStateExecutor
import com.will118.scoville2000.engine.PlantType
import com.will118.scoville2000.engine.Seed
import com.will118.scoville2000.ui.theme.Typography
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun ChilliDex(
    gameStateId: GameId,
    gameStateExecutor: GameStateExecutor,
    currentPlantTypes: SnapshotStateList<PlantType>,
    plantSeed: (Seed) -> Unit,
    autoPlantTechnologyCapable: Boolean,
    autoPlantChecked: (PlantType, Boolean) -> Unit,
    onGameOver: () -> Unit,
) {
    LaunchedEffect(key1 = gameStateId, block = {
        gameStateExecutor.loop(onGameOver)
    })

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "ChilliDEX",
            modifier = Modifier.padding(
                start = 15.dp,
                top = 10.dp,
                bottom = 0.dp,
            ),
            style = Typography.h5,
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(currentPlantTypes) { plantType ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                        .clickable { },
                    elevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier.padding(15.dp)
                    ) {
                        Row {
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
                        StatText(name = "Scovilles", value = plantType.scovilles.toString())
                        Spacer(modifier = Modifier.height(10.dp))
                        StatText(name = "Cost", value = plantType.cost.toString())
                    }
                }
            }
        }
    }
}