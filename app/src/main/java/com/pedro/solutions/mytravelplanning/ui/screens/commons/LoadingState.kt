package com.pedro.solutions.mytravelplanning.ui.screens.commons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pedro.solutions.mytravelplanning.ui.theme.TravelsColors

@Composable
fun LoadingState(modifier: Modifier = Modifier, isLoading: Boolean = true) {
    if (isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier, color = TravelsColors.Brand)
        }
    }
}

@Preview
@Composable
fun LoadingStatePreview() {
    LoadingState()
}