package com.pedro.solutions.mytravelplanning.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TravelButton(modifier: Modifier = Modifier, enabled: Boolean = true, text: String, onClick: () -> Unit) {
    Button(modifier = modifier, enabled = enabled, onClick = onClick) {
        Text(text = text)
    }
}