package com.will118.scoville2000.components.game

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.will118.scoville2000.engine.PlantType
import com.will118.scoville2000.engine.StockLevel
import com.will118.scoville2000.ui.theme.Typography

@ExperimentalFoundationApi
@Composable
fun InventorySection(
    inventory: SnapshotStateMap<PlantType, StockLevel>,
    sell: (PlantType) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Inventory", style = Typography.h5)
        Spacer(modifier = Modifier.height(10.dp))
        Table(
            columns = listOf(
                TableColumn(header = "Type"),
                TableColumn(header = "Peppers"),
                TableColumn(header = ""),
            ),
            items = inventory.asSequence().filter { it.value.peppers > 0 },
            renderItem = { column, item ->
                when (column.index) {
                    0 -> TableCellText(text = item.key.displayName)
                    1 -> TableCellText(text = "${item.value.peppers}")
                    2 -> TextButton(onClick = { sell(item.key) }) {
                        Text(text = "Sell")
                    }
                }
            }
        )
    }
}
