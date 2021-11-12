package com.will118.scoville2000.components.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.will118.scoville2000.engine.Buyer
import com.will118.scoville2000.engine.Currency
import com.will118.scoville2000.engine.Light
import com.will118.scoville2000.engine.Medium
import com.will118.scoville2000.ui.theme.Typography
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
private fun StatText(name: String, value: String) {
    Text(buildAnnotatedString {
        append("$name:")
        append(" ")
        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
            append(value)
        }
    })
}

@Composable
fun StatSection(
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
