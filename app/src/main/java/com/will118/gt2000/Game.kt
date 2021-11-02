package com.will118.gt2000

import Area
import Describe
import Light
import Medium
import Purchasable
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.will118.gt2000.engine.*
import com.will118.gt2000.ui.theme.Typography
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
@Composable
fun Game(gameState: GameState, gameStateExecutor: GameStateExecutor) {
    val date = gameState.date.observeAsState()
    val balance = gameState.balance.observeAsState()
    val inventory = gameState.inventory

    val dividerPadding = Modifier.padding(vertical = 15.dp)

    Column(modifier = Modifier
        .padding(10.dp)
        .fillMaxWidth()
        .verticalScroll(
            state = rememberScrollState(),
            enabled = true
        )) {
        PlantControl(
            plants = gameState.plants,
            area = gameState.area.value,
            date = date.value!!,
            harvest = { gameStateExecutor.enqueue(Harvest(it)) },
            compost = { gameStateExecutor.enqueue(Compost(it)) },
        )
        Divider(modifier = dividerPadding)
        InventoryControl(
            inventory = inventory,
            sell = { gameStateExecutor.enqueue(SellProduce(it)) },
        )
        Divider(modifier = dividerPadding)
        StatsControl(
            balance = balance.value,
            date = date.value,
            light = gameState.light.value,
            medium = gameState.medium.value,
            buyer = gameState.buyer,
        )
        Divider(modifier = dividerPadding)
        ShopControl(
            currentLight = gameState.light.value,
            currentArea = gameState.area.value,
            currentMedium = gameState.medium.value,
            plantSeed = { gameStateExecutor.enqueue(PlantSeed(it)) },
            upgradeLight = { gameStateExecutor.enqueue(UpgradeLight(it)) },
            upgradeMedium = { gameStateExecutor.enqueue(UpgradeMedium(it)) },
            upgradeArea = { gameStateExecutor.enqueue(UpgradeArea(it)) },
        )
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
    @Composable
    fun <T> shopTable(items: Sequence<T>, button: @Composable (T) -> Unit)
        where T : Describe, T : Purchasable {
        Table(
            headers = listOf(null, null, null),
            items = items,
            renderItem = { column, item ->
                when (column.index) {
                    0 -> {
                        Text(
                            text = item.displayName,
                            modifier = TableCellModifier,
                            overflow = TextOverflow.Ellipsis,
                            style = Typography.body2,
                        )
                    }
                    1 -> {
                        Text(
                            text = "$${item.cost!!.total}",
                            modifier = TableCellModifier,
                            overflow = TextOverflow.Ellipsis,
                            style = Typography.body2,
                        )
                    }
                    2 -> button(item)
                }
            }
        )
    }

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
        Text(text = "Growth Medium", style = Typography.subtitle2)
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
fun StatsControl(
    balance: Long?,
    date: Instant?,
    light: Light,
    medium: Medium,
    buyer: Buyer,
) {
    val dateTime = OffsetDateTime.ofInstant(date, ZoneId.systemDefault())

    Column {
        Text(text = "Info", style = Typography.h5)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Date: ${dateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))}")
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Balance: $${balance}")
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Light source: ${light.name}")
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Growth medium: ${medium.name}")
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Price per pepper: $${buyer.pricePerScoville} (${buyer.name})")
    }
}


private val TableCellModifier = Modifier
    .padding(5.dp)
    .fillMaxWidth()

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
            headers = listOf("Item", "Seeds", "Peppers", ""),
            items = inventory.asSequence(),
            renderItem = { column, item ->
                when (column.index) {
                    0 -> {
                        Text(
                            text = item.key.displayName,
                            modifier = TableCellModifier,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    1 -> {
                        Text(
                            text = "${item.value.seeds}",
                            modifier = TableCellModifier,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    2 -> {
                        Text(
                            text = "${item.value.peppers}",
                            modifier = TableCellModifier,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    3 -> {
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
                        modifier = Modifier.fillMaxWidth(),
                        contentColor = Color.Transparent,
                    ) {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = TableCellModifier,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            fontWeight = FontWeight.Black,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }

                // TODO don't iterate 4 times
                for (item in items) {
                    Surface(
                        border = BorderStroke(1.dp, Color.LightGray),
                        contentColor = Color.Transparent,
                        modifier = Modifier
                            .height(35.dp)
                            .fillMaxWidth()
                    ) {
                        renderItem(column, item)
                    }
                }
            }
        }
    }
}

@Composable
fun PlantControl(
    plants: SnapshotStateList<Plant>,
    area: Area,
    date: Instant,
    harvest: (Plant) -> Unit,
    compost: (Plant) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Plants",
            style = Typography.h5
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "${plants.size}/${area.total} (${area.displayName})",
            style = Typography.subtitle2
        )
        Spacer(modifier = Modifier.height(10.dp))
        for (plant in plants) {
            val currentPhase = plant.currentPhase(date)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${plant.plantType.displayName}: ${currentPhase?.displayName ?: "Rotten"}"
                )
                Spacer(modifier = Modifier.width(10.dp))
                if (currentPhase?.isRipe == true) {
                    TextButton(onClick = { harvest(plant) }) {
                        Text(text = "Harvest")
                    }
                } else if (currentPhase == null) {
                    TextButton(onClick = { compost(plant) }) {
                        Text(text = "Compost")
                    }
                }
            }
        }
    }
}