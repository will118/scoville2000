package com.will118.scoville2000.components.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.will118.scoville2000.components.Table
import com.will118.scoville2000.components.TableCellText
import com.will118.scoville2000.components.TableColumn
import com.will118.scoville2000.engine.Describe
import com.will118.scoville2000.engine.Purchasable

@Composable
fun <T> ShopTable(
    items: Sequence<T>,
    tableCellHeight: Dp = 35.dp,
    button: @Composable (T) -> Unit,
) where T : Describe, T : Purchasable {
    Table(
        columns = listOf(
            TableColumn(header = null),
            TableColumn(header = null),
            TableColumn(header = null, weight = 0.75f),
        ),
        items = items,
        renderItem = { column, item ->
            when (column.index) {
                0 -> TableCellText(text = item.displayName)
                1 -> TableCellText(text = item.cost!!.toString())
                2 -> button(item)
            }
        },
        tableCellHeight = tableCellHeight,
    )
}

