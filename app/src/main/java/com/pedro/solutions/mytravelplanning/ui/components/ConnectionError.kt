package com.pedro.solutions.mytravelplanning.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pedro.solutions.mytravelplanning.R
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenTwo

@Composable
fun ConnectionError(
    modifier: Modifier = Modifier,
    isShowing: Boolean = true,
    tryAgainAction: () -> Unit
) {
    if (isShowing) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.connection_error_message))
            Spacer(modifier = Modifier.height(DimenTwo))
            TravelButton(
                text = stringResource(R.string.connection_error_try_again),
                onClick = tryAgainAction
            )
        }
    }
}

@Preview
@Composable
fun connectionErrorPreview() {
    ConnectionError(tryAgainAction = {})
}