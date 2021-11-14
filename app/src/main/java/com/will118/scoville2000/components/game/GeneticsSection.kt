package com.will118.scoville2000.components.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.will118.scoville2000.components.Table
import com.will118.scoville2000.components.TableCellText
import com.will118.scoville2000.components.TableColumn
import com.will118.scoville2000.engine.Distillate
import com.will118.scoville2000.ui.theme.Typography

@Composable
fun GeneticsSection() {
    Column {
        Text(text = "Genetics", style = Typography.h5)
        Spacer(modifier = Modifier.height(10.dp))
        Table(
            columns = listOf(
                TableColumn(header = "Distillate"),
                TableColumn(header = "Required SHU"),
                TableColumn(header = "", weight = 0.75f),
            ),
            items = Distillate.values().asSequence(),
            renderItem = { column, item ->
                when (column.index) {
                    0 -> TableCellText(text = item.displayName)
                    1 -> TableCellText(text = item.requiredScovilles.toString())
                    2 -> TextButton(onClick = { }) {
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