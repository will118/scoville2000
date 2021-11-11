package com.will118.scoville2000

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.will118.scoville2000.engine.*
import com.will118.scoville2000.engine.Area
import com.will118.scoville2000.engine.Light
import com.will118.scoville2000.engine.Medium
import com.will118.scoville2000.ui.theme.Typography
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@ExperimentalGraphicsApi
@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
@Composable
fun Game(
    area: Area,
    balance: Currency,
    buyer: Buyer,
    dateMillis: Long,
    light: Light,
    medium: Medium,
    technologyLevel: TechnologyLevel,
    plantPots: SnapshotStateList<PlantPot>,
    inventory: SnapshotStateMap<PlantType, StockLevel>,
    harvest: (PlantPot) -> Unit,
    compost: (PlantPot) -> Unit,
    sell: (PlantType) -> Unit,
    upgradeArea: (Area) -> Unit,
    upgradeMedium: (Medium) -> Unit,
    upgradeLight: (Light) -> Unit,
    plantSeed: (Seed) -> Unit,
) {
    val dividerPadding = Modifier.padding(vertical = 15.dp)

    Column(modifier = Modifier
        .padding(10.dp)
        .fillMaxWidth()
        .verticalScroll(
            state = rememberScrollState(),
            enabled = true
        )) {
        PlantControl(
            plantPots = plantPots,
            area = area,
            dateMillis = dateMillis,
            harvest = harvest,
            compost = compost,
        )
        Divider(modifier = dividerPadding)
        InventoryControl(
            inventory = inventory,
            sell = sell,
        )
        Divider(modifier = dividerPadding)
        StatsControl(
            balance = balance,
            dateMillis = dateMillis,
            light = light,
            medium = medium,
            buyer = buyer,
        )
        Divider(modifier = dividerPadding)
        ShopControl(
            currentLight = light,
            currentArea = area,
            currentMedium = medium,
            plantSeed = plantSeed,
            upgradeLight = upgradeLight,
            upgradeMedium = upgradeMedium,
            upgradeArea = upgradeArea,
        )
        if (technologyLevel != TechnologyLevel.None) {
            Divider(modifier = dividerPadding)
            TechnologyControl(
                technologyLevel = technologyLevel
            )
        }
    }
}

@Composable
fun <T> shopTable(items: Sequence<T>, button: @Composable (T) -> Unit)
        where T : Describe, T : Purchasable {
    Table(
        headers = listOf(null, null, null),
        items = items,
        renderItem = { column, item ->
            when (column.index) {
                0 -> TableCellText(text = item.displayName)
                1 -> TableCellText(text = item.cost!!.toString())
                2 -> button(item)
            }
        }
    )
}

@Composable
fun TechnologyControl(
    technologyLevel: TechnologyLevel,
) {
    Column {
        Text(text = "Technology", style = Typography.h5)
        Spacer(modifier = Modifier.height(10.dp))
        shopTable(
            items = technologyLevel
                .visibleTechnologies()
                .asSequence()
        ) {
            TextButton(onClick = {  }) {
                Text(
                    text = "Buy",
                )
            }
        }
    }
}

@Composable
fun ShopControl(
    currentLight: Light,
    currentArea: Area,
    currentMedium: Medium,
    plantSeed: (Seed) -> Unit,
    upgradeLight: (Light) -> Unit,
    upgradeMedium: (Medium) -> Unit,
    upgradeArea: (Area) -> Unit,
) {
    Column {
        Text(text = "Shop", style = Typography.h5)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Seeds", style = Typography.subtitle2)
        Spacer(modifier = Modifier.height(5.dp))
        shopTable(
            items = PlantType.values()
                .filter { it.cost != null }
                .asSequence()
        ) {
            TextButton(onClick = { plantSeed(it.toSeed()) }) {
                Text(
                    text = "Plant",
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Lights", style = Typography.subtitle2)
        Spacer(modifier = Modifier.height(5.dp))
        shopTable(
            items = Light.values()
                .dropWhile { it.ordinal <= currentLight.ordinal }
                .asSequence()
        ) {
            TextButton(onClick = { upgradeLight(it) }) {
                Text(
                    text = "Upgrade",
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Growth medium", style = Typography.subtitle2)
        Spacer(modifier = Modifier.height(5.dp))
        shopTable(
            items = Medium.values()
                .dropWhile { it.ordinal <= currentMedium.ordinal }
                .asSequence()
        ) {
            TextButton(onClick = { upgradeMedium(it) }) {
                Text(
                    text = "Upgrade",
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Area", style = Typography.subtitle2)
        Spacer(modifier = Modifier.height(5.dp))
        shopTable(
            items = Area.values()
                .dropWhile { it.ordinal <= currentArea.ordinal }
                .asSequence()
        ) {
            TextButton(onClick = { upgradeArea(it) }) {
                Text(
                    text = "Upgrade",
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun StatText(name: String, value: String) {
    Text(buildAnnotatedString {
        append("$name:")
        append(" ")
        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
            append(value)
        }
    })
}

@Composable
fun StatsControl(
    balance: Currency,
    dateMillis: Long,
    light: Light,
    medium: Medium,
    buyer: Buyer,
) {
    val dateTime = OffsetDateTime.ofInstant(
        Instant.ofEpochMilli(dateMillis),
        ZoneId.systemDefault()
    )

    Column {
        Text(text = "Info", style = Typography.h5)
        Spacer(modifier = Modifier.height(10.dp))
        StatText(
            name = "Date",
            value = dateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
        )
        Spacer(modifier = Modifier.height(10.dp))
        StatText(
            name = "Balance",
            value = balance.toString(),
        )
        Spacer(modifier = Modifier.height(10.dp))
        StatText(
            name = "Light source",
            value = light.name,
        )
        Spacer(modifier = Modifier.height(10.dp))
        StatText(
            name = "Growth medium",
            value = medium.name,
        )
        Spacer(modifier = Modifier.height(10.dp))
        StatText(
            name = "Price",
            value = "${buyer.pricePerScoville}/milliscoville (${buyer.displayName})",
        )
    }
}


private val TableCellModifier: @Composable BoxScope.() -> Modifier = {
    Modifier
//        .padding(5.dp)
//        .fillMaxWidth()
//        .fillMaxHeight()
//        .align(Alignment.Center)
}

@Composable
private fun TableCellText(text: String) {
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Text(
            text = text,
            modifier = TableCellModifier(),
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            style = Typography.body2,
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun InventoryControl(
    inventory: SnapshotStateMap<PlantType, StockLevel>,
    sell: (PlantType) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Inventory", style = Typography.h5)
        Spacer(modifier = Modifier.height(10.dp))
        Table(
            headers = listOf("Type", "Peppers", ""),
            items = inventory.asSequence().filter { it.value.peppers > 0 },
            renderItem = { column, item ->
                when (column.index) {
                    0 -> TableCellText(text = item.key.displayName)
                    1 -> TableCellText(text = "${item.value.peppers}")
                    2 -> {
                        TextButton(onClick = { sell(item.key) }) {
                            Text(
                                text = "Sell",
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun <T> Table(
    headers: List<String?>,
    items: Sequence<T>,
    renderItem: @Composable (IndexedValue<String?>, T) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        for (column in headers.withIndex()) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                column.value?.let {
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
                                modifier = TableCellModifier(),
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
                                .height(35.dp)
                                .fillMaxWidth()
                        ) {
                            renderItem(column, item)
                        }
                    }
                } else {
                    Surface(
                        border = BorderStroke(1.dp, Color.LightGray),
                        modifier = Modifier
                            .height(35.dp)
                            .fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {}
                    }
                }
            }
        }
    }
}

@ExperimentalGraphicsApi
@Composable
fun PlantControl(
    plantPots: SnapshotStateList<PlantPot>,
    area: Area,
    dateMillis: Long,
    harvest: (PlantPot) -> Unit,
    compost: (PlantPot) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Plants",
            style = Typography.h5
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "${plantPots.count { it.plant != null }}/${area.total} (${area.displayName})",
            style = Typography.subtitle2
        )
        Spacer(modifier = Modifier.height(10.dp))
        PlantGrid(
            area = area,
            plantPots = plantPots,
            dateMillis = dateMillis,
            harvest = harvest,
            compost = compost
        )
    }
}