package com.will118.scoville2000.components.gameover

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.will118.scoville2000.ui.theme.Typography

@Composable
fun GameOver(onRestart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Game Over", style = Typography.h3)
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = { onRestart() }) {
            Text(text = "Restart")
        }
    }
}
