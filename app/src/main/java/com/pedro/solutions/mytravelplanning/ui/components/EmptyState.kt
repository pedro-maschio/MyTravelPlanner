package com.pedro.solutions.mytravelplanning.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.pedro.solutions.mytravelplanning.R
import com.pedro.solutions.mytravelplanning.ui.theme.TravelsColors
import com.pedro.solutions.mytravelplanning.ui.theme.Typography
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenFive
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenOne
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenTwo

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    isEmpty: Boolean = true,
    title: String,
    supportingText: String? = null
) {
    if (isEmpty) {
        Column(
            modifier = modifier
                .padding(horizontal = DimenTwo),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(DimenFive),
                imageVector = Icons.Default.WarningAmber,
                tint = TravelsColors.Brand,
                contentDescription = null
            )
            Text(
                text = title,
                modifier = Modifier.padding(top = DimenTwo),
                style = Typography.titleMedium.copy(fontWeight = FontWeight.Medium)
            )

            supportingText?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(top = DimenOne),
                    style = Typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                )
            }
        }
    }
}

@Preview
@Composable
fun EmptyStatePreview(modifier: Modifier = Modifier) {
    EmptyState(
        title = stringResource(R.string.travels_listing_empty_message),
        supportingText = "Comece agora mesmo a criar sua viagem!"
    )
}