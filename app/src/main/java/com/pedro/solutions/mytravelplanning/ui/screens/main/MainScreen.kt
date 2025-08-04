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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pedro.solutions.mytravelplanning.R
import com.pedro.solutions.mytravelplanning.ui.screens.commons.EmptyState
import com.pedro.solutions.mytravelplanning.ui.screens.commons.TravelAppBar
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

    EmptyState(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = DimenFive),
        isEmpty = uiState.value.shouldShowEmptyState,
        title = stringResource(R.string.travels_listing_empty_message),
        supportingText = stringResource(R.string.travels_listing_empty_supporting_text)
    )

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
    fun SelectionModeAction(modifier: Modifier = Modifier) {
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
                            Text(text = stringResource(R.string.create_travel_delete_travel))
                        }, onClick = {
                            viewModel.onDeleteTravelsClick()
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
        }
    }
}

@Composable
fun TravelCard(
    travel: MainScreenTravel,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
            .padding(all = DimenTwo)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        colors = CardDefaults.cardColors(containerColor = if (travel.isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = DimenOne),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = travel.travelName.ifBlank { stringResource(R.string.main_screen_unnamed_travel) })
        }
    }

}