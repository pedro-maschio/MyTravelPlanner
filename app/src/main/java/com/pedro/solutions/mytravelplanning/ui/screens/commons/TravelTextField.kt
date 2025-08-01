package com.pedro.solutions.mytravelplanning.ui.screens.commons

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenTwo

@Composable
fun TravelTextField(
    modifier: Modifier = Modifier,
    value: String,
    colors: TextFieldColors = TextFieldDefaults.colors(
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent
    ),
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit = {}
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .focusable(enabled),
        enabled = enabled,
        shape = RoundedCornerShape(size = DimenTwo),
        colors = colors,
        value = value,
        trailingIcon = trailingIcon,
        onValueChange = onValueChange,
        readOnly = !enabled,
    )
}