package com.will118.scoville2000.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

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
