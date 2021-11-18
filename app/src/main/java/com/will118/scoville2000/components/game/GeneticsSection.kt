package com.will118.scoville2000.components.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.will118.scoville2000.components.StatText
import com.will118.scoville2000.engine.Distillate
import com.will118.scoville2000.engine.StockLevel
import com.will118.scoville2000.ui.theme.Typography

@ExperimentalGraphicsApi
@Composable
fun GeneticsSection(
    distillateInventory: SnapshotStateMap<Distillate, StockLevel>,
) {
    val quantumCaps = distillateInventory.getOrDefault(Distillate.QuantumCapsicum, StockLevel(0))

    val (progress, setProgress) = remember { mutableStateOf(20f) }

    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value

    Column {
        Text(text = "Genetics", style = Typography.h5)
        StatText(name = "Quantum Capsicum", value = quantumCaps.toString())
        Spacer(modifier = Modifier.height(10.dp))

        CircularProgressBar(
            modifier = Modifier.size(120.dp),
            progress = animatedProgress,
            progressMax = 100f,
            progressBarColor = Color.hsv(93f, 0.60f, 0.78f),
            progressBarWidth = 12.dp,
            backgroundProgressBarColor = Color.Gray,
            backgroundProgressBarWidth = 5.dp,
            roundBorder = true,
            startAngle = 0f
        )
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.Center
//        ) {
//            for (i in 0 until 3) {
//                Column(
//                    modifier = Modifier.weight(1.0f),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                ) {
//                    for (i in 0 until 2) {
//                        Surface(
//                            border = BorderStroke(1.dp, Color.LightGray),
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .aspectRatio(1.0f)
//                        ) {
//                            Box(modifier = Modifier.fillMaxSize()) {}
//                        }
//                    }
//                }
//            }
//        }
    }
}

// Yield
// Hallucinogenics
@ExperimentalGraphicsApi
@Preview
@Composable
fun GeneticsPreview() {
    val distillateInventory = remember {
        mutableStateMapOf(
            Distillate.QuantumCapsicum to StockLevel(5)
        )
    }
    GeneticsSection(distillateInventory = distillateInventory)
}