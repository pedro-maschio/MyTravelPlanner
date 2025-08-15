package com.pedro.solutions.mytravelplanning.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pedro.solutions.mytravelplanning.ui.theme.latoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(text = title, fontFamily = latoFontFamily)
        }, navigationIcon = navigationIcon, actions = actions
    )
}