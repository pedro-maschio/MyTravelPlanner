package com.pedro.solutions.mytravelplanning.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pedro.solutions.mytravelplanning.R

@Composable
fun TravelDialog(
    modifier: Modifier = Modifier,
    title: String,
    message: @Composable () -> Unit = { },
    icon: @Composable () -> Unit = { Icon(Icons.Default.Info, contentDescription = null) },
    confirmButtonText: String = stringResource(R.string.travels_listing_delete_dialog_confirm_message),
    cancelButtonText: String = stringResource(R.string.travels_listing_delete_dialog_cancel_message),
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: (() -> Unit)? = null
) {
    AlertDialog(
        modifier = modifier, icon =
        icon, title = {
        Text(text = title)
    }, text = message, onDismissRequest = onDismiss, confirmButton = {
        TextButton(
            onClick = onConfirm
        ) {
            Text(text = confirmButtonText)
        }
    }, dismissButton = {
        onCancel?.let {
            TextButton(
                onClick = onCancel
            ) {
                Text(text = cancelButtonText)
            }
        }
    })
}