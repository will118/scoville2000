package com.will118.scoville2000.components.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.will118.scoville2000.engine.Describe
import com.will118.scoville2000.engine.Purchasable
import com.will118.scoville2000.ui.theme.Typography

@Composable
fun TableCellText(text: String) {
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Text(
            text = text,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            style = Typography.body2,
        )
    }
}

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
            TableColumn(header = null),
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

data class TableColumn(val header: String?, val weight: Float = 1.0f)

@Composable
fun <T> Table(
    columns: List<TableColumn>,
    items: Sequence<T>,
    renderItem: @Composable (IndexedValue<TableColumn>, T) -> Unit,
    tableCellHeight: Dp = 35.dp,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        for (column in columns.withIndex()) {
            Column(
                modifier = Modifier.weight(column.value.weight),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                column.value.header?.let {
                    Surface(
                        border = BorderStroke(1.dp, Color.LightGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(25.dp),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = it,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }

                if (items.any()) {
                    // TODO don't iterate 4 times
                    for (item in items) {
                        Surface(
                            border = BorderStroke(1.dp, Color.LightGray),
                            modifier = Modifier
                                .height(tableCellHeight)
                                .fillMaxWidth()
                        ) {
                            renderItem(column, item)
                        }
                    }
                } else {
                    Surface(
                        border = BorderStroke(1.dp, Color.LightGray),
                        modifier = Modifier
                            .height(tableCellHeight)
                            .fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {}
                    }
                }
            }
        }
    }
}
