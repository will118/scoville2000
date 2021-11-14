package com.will118.scoville2000.components.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.will118.scoville2000.components.StatText
import com.will118.scoville2000.engine.*
import com.will118.scoville2000.ui.theme.Typography
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun StatSection(
    balance: Currency,
    dateMillis: Long,
    light: Light,
    medium: Medium,
    buyer: Buyer,
    technologies: SnapshotStateList<Technology>,
) {
    val dateTime = OffsetDateTime.ofInstant(
        Instant.ofEpochMilli(dateMillis),
        ZoneId.systemDefault()
    )

    val autoPlanters = technologies.count { it == Technology.AutoPlanter }

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
        if (autoPlanters > 0) {
            Spacer(modifier = Modifier.height(10.dp))
            StatText(
                name = "AutoPlanters",
                value = "$autoPlanters",
            )
        }
    }
}
