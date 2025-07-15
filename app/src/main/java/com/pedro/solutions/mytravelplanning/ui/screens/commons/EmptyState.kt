package com.pedro.solutions.mytravelplanning.ui.screens.commons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.pedro.solutions.mytravelplanning.ui.theme.TravelsColors
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenThree
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenTwo

@Composable
fun EmptyState(modifier: Modifier = Modifier, isEmpty: Boolean = true, message: String) {
    if (isEmpty) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = DimenTwo),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Default.Info, tint = TravelsColors.Brand, contentDescription = null)
            Text(text = message, modifier=Modifier.padding(top=DimenTwo))
        }
    }
}

@Preview
@Composable
fun EmptyStatePreview(modifier: Modifier = Modifier) {
    EmptyState(message = "Não há nada cadastrado ainda, clique no botão abaixo para cadastrar")
}