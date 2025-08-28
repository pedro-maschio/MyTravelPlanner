package com.pedro.solutions.mytravelplanning.ui.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pedro.solutions.mytravelplanning.R
import com.pedro.solutions.mytravelplanning.ui.components.EmptyState
import com.pedro.solutions.mytravelplanning.ui.components.TravelAppBar
import com.pedro.solutions.mytravelplanning.ui.components.TravelCard
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenFive
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenOne
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = koinViewModel(),
    onClickTravelItem: (Long) -> Unit,
    onClickFloatingButton: () -> Unit,
    onGoBack: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadTravels()
    }

    BackHandler {
        if (uiState.value.isOnSelectionMode) {
            viewModel.setOnSelectionMode(false)
        } else {
            viewModel.goBack()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest {
            when (it) {
                is MainScreenUiEvent.OpenCreateTravelScreen -> onClickTravelItem(it.travelId)
                is MainScreenUiEvent.GoBack -> onGoBack()
            }
        }
    }

    if (uiState.value.isDeleteDialogShowing) {
        AlertDialog(icon = {
            Icon(Icons.Default.Info, contentDescription = null)
        }, title = {
            Text(text = stringResource(R.string.travels_listing_delete_dialog_title))
        }, text = {
            Text(
                text = stringResource(
                    R.string.travels_listing_delete_dialog_message,
                    uiState.value.selectedTravelIds.size
                )
            )
        }, onDismissRequest = {
            viewModel.hideDeleteDialog()
        }, confirmButton = {
            TextButton(
                onClick = {
                    viewModel.onDeleteTravelsClick()
                }) {
                Text(text = stringResource(R.string.travels_listing_delete_dialog_confirm_message))
            }
        }, dismissButton = {
            TextButton(
                onClick = {
                    viewModel.hideDeleteDialog()
                }) {
                Text(text = stringResource(R.string.travels_listing_delete_dialog_cancel_message))
            }
        })
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

    Scaffold(modifier = modifier, topBar = {
        if (uiState.value.isOnSelectionMode) {
            TravelAppBar(
                modifier = Modifier.padding(vertical = DimenOne),
                title = topBarText,
                actions = { SelectionModeAction() })
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DimenOne, vertical = DimenOne),
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = uiState.value.searchTerm,
                            onQueryChange = { viewModel.updateQuery(it) },
                            onSearch = {
                                viewModel.updateQuery(it)
                            },
                            expanded = uiState.value.isSearchScreenExpanded,
                            onExpandedChange = { viewModel.setSearchScreenExpanded(it) },
                            placeholder = { Text(text = stringResource(R.string.travels_listing_search_travel_placeholder)) },
                            leadingIcon = {
                                IconButton(onClick = {
                                    if (uiState.value.isSearchScreenExpanded) {
                                        viewModel.setSearchScreenExpanded(false)
                                    }
                                }, enabled = uiState.value.isSearchScreenExpanded) {
                                    Icon(
                                        imageVector = if (uiState.value.isSearchScreenExpanded) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Search,
                                        contentDescription = null
                                    )
                                }
                            },
                            trailingIcon = {
                                if (uiState.value.searchTerm.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.clearSearchQuery() }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = null
                                        )
                                    }
                                }
                            })
                    },
                    expanded = uiState.value.isSearchScreenExpanded,
                    onExpandedChange = { viewModel.setSearchScreenExpanded(it) },
                ) {
                    LazyColumn {
                        items(uiState.value.searchedTravels) { travel ->
                            TravelCard(travel = travel, onLongClick = {
                                // User won`t be able to select during search!
                            }, onClick = {
                                viewModel.openCreateTravelScreen(travel.travelId)
                            })
                        }
                    }
                }
            }
        }
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = onClickFloatingButton
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }) { innerPadding ->
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
                        viewModel.openCreateTravelScreen(travel.travelId)
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