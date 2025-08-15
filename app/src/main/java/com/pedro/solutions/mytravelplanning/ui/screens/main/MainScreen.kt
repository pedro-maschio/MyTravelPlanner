package com.pedro.solutions.mytravelplanning.ui.screens.main

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pedro.solutions.mytravelplanning.R
import com.pedro.solutions.mytravelplanning.ui.components.EmptyState
import com.pedro.solutions.mytravelplanning.ui.components.TravelAppBar
import com.pedro.solutions.mytravelplanning.ui.theme.Typography
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenFive
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenOne
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenTwo
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = koinViewModel(),
    onClickTravelItem: (Long) -> Unit,
    onClickFloatingButton: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadTravels()
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest {
            when (it) {
                is MainScreenUiEvent.OpenCreateTravelScreen -> onClickTravelItem(it.travelId)
            }
        }
    }

    if (uiState.value.isDeleteDialogShowing) {
        AlertDialog(
            icon = {
                Icon(Icons.Default.Info, contentDescription = null)
            },
            title = {
                Text(text = stringResource(R.string.travels_listing_delete_dialog_title))
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.travels_listing_delete_dialog_message,
                        uiState.value.selectedTravelIds.size
                    )
                )
            },
            onDismissRequest = {
                viewModel.hideDeleteDialog()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onDeleteTravelsClick()
                    }
                ) {
                    Text(text = stringResource(R.string.travels_listing_delete_dialog_confirm_message))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.hideDeleteDialog()
                    }
                ) {
                    Text(text = stringResource(R.string.travels_listing_delete_dialog_cancel_message))
                }
            }
        )
    }

    val topBarText = if (uiState.value.isOnSelectionMode) {
        pluralStringResource(
            R.plurals.travels_listing_selected_items,
            uiState.value.selectedTravelIds.size,
            uiState.value.selectedTravelIds.size
        )
    } else {
        stringResource(R.string.travels_listing_title)
    }

    @Composable
    fun SelectionModeAction() {
        uiState.value.isOnSelectionMode.takeIf { it }?.let {
            IconButton(onClick = {
                viewModel.setDropdownMenuShowing(true)
            }) {
                Icon(
                    imageVector = Icons.Default.MoreVert, contentDescription = null
                )
                Box(contentAlignment = Alignment.TopEnd) {
                    DropdownMenu(
                        expanded = uiState.value.isDropDownMenuShowing, onDismissRequest = {
                            viewModel.setDropdownMenuShowing(false)
                        }) {
                        DropdownMenuItem(text = {
                            Text(text = stringResource(R.string.travels_listing_delete_dropdown_title))
                        }, onClick = {
                            viewModel.showDeleteDialog()
                        })
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { TravelAppBar(title = topBarText, actions = { SelectionModeAction() }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick =
                    onClickFloatingButton
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            items(uiState.value.travels) { travel ->
                TravelCard(travel = travel, onLongClick = {
                    if (!uiState.value.isOnSelectionMode) {
                        viewModel.setOnSelectionMode(true)
                    }
                    viewModel.selectTravel(travel.travelId)
                }, onClick = {
                    if (uiState.value.isOnSelectionMode) {
                        viewModel.selectTravel(travel.travelId)
                    } else {
                        viewModel.openTravelDetail(travel.travelId)
                    }
                })
            }
            item {
                EmptyState(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = DimenFive),
                    isEmpty = uiState.value.shouldShowEmptyState,
                    title = stringResource(R.string.travels_listing_empty_message),
                    supportingText = stringResource(R.string.travels_listing_empty_supporting_text)
                )
            }
        }
    }
}

@Composable
fun TravelCard(
    travel: MainScreenTravel,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
            .padding(all = DimenTwo)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        colors = CardDefaults.cardColors(containerColor = if (travel.isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = DimenOne),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = travel.travelName.ifBlank { stringResource(R.string.travels_listing_unnamed_travel) },
                style = Typography.titleMedium
            )
        }
    }
}

@Preview
@Composable
fun TravelCardPreview(modifier: Modifier = Modifier) {
    TravelCard(
        travel = MainScreenTravel(
            travelName = "Viagem para a disney",
            travelId = -1,
            isSelected = false
        ), onLongClick = { }) { }
}