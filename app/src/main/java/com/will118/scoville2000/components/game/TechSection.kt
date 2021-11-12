package com.will118.scoville2000.components.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.will118.scoville2000.engine.Technology
import com.will118.scoville2000.engine.TechnologyLevel
import com.will118.scoville2000.ui.theme.Typography

@Composable
fun TechSection(
    technologyLevel: TechnologyLevel,
    purchaseTechnology: (Technology) -> Unit,
) {
    Column {
        Text(text = "Technology", style = Typography.h5)
        Spacer(modifier = Modifier.height(10.dp))
        ShopTable(
            items = technologyLevel
                .visibleTechnologies()
                .asSequence(),
            tableCellHeight = 45.dp
        ) {
            TextButton(onClick = { purchaseTechnology(it) }) {
                Text(text = "Buy")
            }
        }
    }
}
