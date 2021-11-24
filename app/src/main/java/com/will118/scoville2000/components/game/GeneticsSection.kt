package com.will118.scoville2000.components.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.will118.scoville2000.engine.*
import com.will118.scoville2000.ui.theme.Typography

@ExperimentalComposeUiApi
@ExperimentalGraphicsApi
@Composable
fun GeneticsSection(
    geneticComputationState: GeneticComputationState,
    setLeftPlantType: (PlantType) -> Unit,
    setRightPlantType: (PlantType) -> Unit,
    distillateInventory: SnapshotStateMap<Distillate, FractionalStockLevel>,
    plantTypes: SnapshotStateList<PlantType>,
    updateFitnessSlider: (GeneticTrait, Float) -> Unit,
) {
    val quantumCaps = distillateInventory.getOrDefault(
        Distillate.QuantumCapsicum,
        FractionalStockLevel(quantity = 0, thousandths = 0),
    )

    val (progress, setProgress) = remember { mutableStateOf(20f) }

    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value

    var leftExpanded by remember { mutableStateOf(false) }
    var rightExpanded by remember { mutableStateOf(false) }

    Column {
        Text(text = "Genetics", style = Typography.h5)
        Spacer(modifier = Modifier.height(10.dp))

        Row {
            Box(modifier = Modifier.weight(0.5f)) {
                Text(
                    text = geneticComputationState.leftPlantType.displayName,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { leftExpanded = true })
                )

                DropdownMenu(
                    expanded = leftExpanded,
                    onDismissRequest = { leftExpanded = false },
                ) {
                    plantTypes.forEachIndexed { index, pt ->
                        DropdownMenuItem(onClick = {
                            setLeftPlantType(plantTypes[index])
                            leftExpanded = false
                        }) {
                            Text(text = pt.displayName)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(5.dp))
            Text("x")
            Spacer(modifier = Modifier.width(5.dp))

            Box(modifier = Modifier.weight(0.5f)) {
                Text(
                    text = geneticComputationState.rightPlantType.displayName,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { rightExpanded = true })
                )

                DropdownMenu(
                    expanded = rightExpanded,
                    onDismissRequest = { rightExpanded = false },
                ) {
                    plantTypes.forEachIndexed { index, pt ->
                        DropdownMenuItem(onClick = {
                            setRightPlantType(plantTypes[index])
                            rightExpanded = false
                        }) {
                            Text(text = pt.displayName)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row {
            Spacer(modifier = Modifier.weight(1.0f))
            CircularProgressBar(
                centerText = "$quantumCaps qcaps",
                modifier = Modifier.size(180.dp),
                progress = animatedProgress,
                progressMax = 100f,
                progressBarColor = if (geneticComputationState.isActive)
                    Color.hsv(93f, 0.60f, 0.78f)
                else
                    Color.LightGray,
                progressBarWidth = 15.dp,
                backgroundProgressBarColor = Color.Gray,
                backgroundProgressBarWidth = 7.dp,
                roundBorder = true,
                startAngle = 0f
            )
            Spacer(modifier = Modifier.weight(1.0f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        GeneticFitnessSliders(
            fitnessFunction = geneticComputationState.fitnessFunction,
            updateFitnessSlider = updateFitnessSlider,
        )
    }
}

@ExperimentalComposeUiApi
@Composable
fun GeneticFitnessSliders(
    fitnessFunction: FitnessFunction,
    updateFitnessSlider: (GeneticTrait, Float) -> Unit
) {
    Row {
        Column {
            for (trait in FitnessFunction.TRAITS) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = trait.displayName,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.20f)
                    )
                    Slider(
                        modifier = Modifier.weight(0.70f),
                        value = fitnessFunction.getValue(trait),
                        onValueChange = { updateFitnessSlider(trait, it) }
                    )
                    Spacer(modifier = Modifier.weight(0.10f))
                }
            }
        }
    }
}
