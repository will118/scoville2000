package com.will118.scoville2000.components.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.will118.scoville2000.components.Table
import com.will118.scoville2000.components.TableCellText
import com.will118.scoville2000.components.TableColumn
import com.will118.scoville2000.engine.*
import com.will118.scoville2000.ui.theme.Typography

@Composable
fun DistillerySection(
    pepperInventory: SnapshotStateMap<PlantType, StockLevel>,
    distillates: List<Distillate>,
    distill: (Distillate) -> Unit,
) {
    val totalScovilles = pepperInventory.totalScovilles()
    Column {
        Text(text = "Distillery", style = Typography.h5)
        Spacer(modifier = Modifier.height(10.dp))
        Table(
            columns = listOf(
                TableColumn(header = "Distillate"),
                TableColumn(header = "Required SHU"),
                TableColumn(header = "", weight = 0.75f),
            ),
            items = distillates.asSequence(),
            renderItem = { column, item ->
                when (column.index) {
                    0 -> TableCellText(text = item.displayName)
                    1 -> TableCellText(text = fmtLong(item.requiredScovilles))
                    2 -> TextButton(
                        enabled = totalScovilles >= item.requiredScovilles,
                        onClick = { distill(item) },
                    ) {
                        Text(
                            text = "Distill",
                        )
                    }
                }
            },
            tableCellHeight = 40.dp,
        )
    }
}
