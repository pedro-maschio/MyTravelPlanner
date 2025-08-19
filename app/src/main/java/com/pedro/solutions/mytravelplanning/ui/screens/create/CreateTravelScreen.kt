package com.pedro.solutions.mytravelplanning.ui.screens.create

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pedro.solutions.mytravelplanning.R
import com.pedro.solutions.mytravelplanning.data.models.TravelType
import com.pedro.solutions.mytravelplanning.ui.components.LoadingState
import com.pedro.solutions.mytravelplanning.ui.components.TravelAppBar
import com.pedro.solutions.mytravelplanning.ui.components.TravelButton
import com.pedro.solutions.mytravelplanning.ui.components.TravelTextField
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenHalf
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenOne
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenSix
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenTwo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateTravelScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateTravelViewModel = koinViewModel(),
    travelId: Long? = null,
    isEditing: Boolean = false,
    onTravelCreated: () -> Unit,
    onTravelDeleted: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current

    val scrollChannel = Channel<Float>()

    LaunchedEffect(listState) {
        while (true) {
            val diff = scrollChannel.receive()
            listState.scrollBy(diff)
        }
    }

    BackHandler {
        // TODO: fix the focus from the keyboard, when the user clicks back
        if (uiState.value.isEditing) {
            viewModel.setIsEditing(false)
        } else {
            viewModel.createTravel()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is CreateTravelUiEvent.OnTravelDeleted -> onTravelDeleted()
                is CreateTravelUiEvent.OnTravelCreated -> onTravelCreated()
            }

        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadTravel(travelId)
        viewModel.setEditingState(isEditing)
        viewModel.addDefaultDay()
    }

    if (uiState.value.isDeleteDialogShowing) {
        AlertDialog(icon = {
            Icon(Icons.Default.Info, contentDescription = null)
        }, title = {
            Text(text = stringResource(R.string.create_travel_delete_dialog_title))
        }, text = {
            Text(
                text = stringResource(
                    R.string.create_travel_delete_dialog_message,
                )
            )
        }, onDismissRequest = {
            viewModel.hideDeleteDialog()
        }, confirmButton = {
            TextButton(
                onClick = {
                    viewModel.deleteTravel()
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


    CreateTravelScaffold { innerPadding ->
        if (uiState.value.isLoading) {
            LoadingState(isLoading = true)
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = DimenTwo),
                verticalArrangement = Arrangement.spacedBy(DimenOne),
                state = listState,
            ) {
                item {
                    TravelTextField(
                        modifier = Modifier.padding(vertical = DimenOne),
                        value = uiState.value.travelName,
                        placeHolder = stringResource(R.string.create_travel_name_placeholder),
                        onValueChange = {
                            viewModel.updateTravelName(travelName = it)
                        })
                }

                itemsIndexed(
                    items = uiState.value.travels,
                    key = { index, _ -> uiState.value.travels[index].id },
                    contentType = { index, _ -> uiState.value.travels[index] }) { index, item ->
                    Row(
                        modifier = Modifier.padding(vertical = DimenHalf),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (item) {
                            is TravelType.Day -> {
                                TravelTextField(
                                    modifier = Modifier.weight(1f),
                                    value = item.title,
                                    onValueChange = {
                                        viewModel.updateTravelDayText(
                                            index = item.index, newText = it
                                        )
                                    },
                                    enabled = uiState.value.isEditing.not()
                                )
                                if (uiState.value.isEditing) {
                                    Icon(
                                        modifier = Modifier
                                            .padding(horizontal = DimenOne)
                                            .clickable {
                                                viewModel.deleteDay(item.index)
                                            },
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null
                                    )
                                } else {
                                    Icon(
                                        modifier = Modifier
                                            .padding(horizontal = DimenOne)
                                            .clickable {
                                                viewModel.addActivity(item.index)
                                                focusManager.clearFocus()
                                            },
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null
                                    )
                                }
                            }

                            is TravelType.Activity -> {
                                val modifier = if (uiState.value.draggingIndex == index) {
                                    Modifier
                                        .zIndex(1f)
                                        .graphicsLayer {
                                            translationY = uiState.value.draggingAmount
                                        }
                                } else {
                                    Modifier
                                }
                                TravelTextField(
                                    modifier = modifier
                                        .weight(1f)
                                        .padding(end = DimenOne),
                                    value = item.title,
                                    enabled = uiState.value.isEditing.not(),
                                    onValueChange = {
                                        viewModel.updateTravelActivityText(
                                            dayIndex = item.dayIndex,
                                            activityIndex = item.index,
                                            newText = it
                                        )
                                    })
                                if (uiState.value.isEditing) {
                                    DraggableIcon(
                                        viewModel = viewModel,
                                        listState = listState,
                                        index = index,
                                        scrollChannel = scrollChannel
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    if (!uiState.value.isEditing) {
                        TravelButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(DimenSix)
                                .padding(bottom = DimenTwo),
                            text = stringResource(R.string.create_travel_add_new_day)
                        ) {
                            focusManager.clearFocus()
                            viewModel.addDay()
                        }
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DraggableIcon(
    modifier: Modifier = Modifier,
    viewModel: CreateTravelViewModel,
    listState: LazyListState,
    index: Int,
    scrollChannel: Channel<Float>
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    Icon(
        modifier = modifier
            .padding(horizontal = DimenOne)
            .pointerInput(listState) {
                detectDragGesturesAfterLongPress(onDragStart = { offset ->
                    val draggingItemInfo: LazyListItemInfo? =
                        listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
                    viewModel.updateDraggingItem(
                        draggingItemInfo
                    )
                    viewModel.updateDraggableItemIndex(index)
                }, onDragEnd = {
                    viewModel.resetDragging()
                }, onDragCancel = {
                    viewModel.resetDragging()
                }, onDrag = { change, dragAmount ->
                    change.consume()
                    viewModel.updateDragAmountY(dragAmount.y)
                    val currentDragItem =
                        uiState.value.draggingItem ?: return@detectDragGesturesAfterLongPress
                    val currentDragIndex =
                        uiState.value.draggingIndex ?: return@detectDragGesturesAfterLongPress

                    val startOffset = currentDragItem.offset + uiState.value.draggingAmount
                    val endOffset =
                        currentDragItem.offset + currentDragItem.size + uiState.value.draggingAmount
                    val middleOffset = startOffset + (endOffset - startOffset) / 2

                    val targetItem = listState.layoutInfo.visibleItemsInfo.find { item ->
                        middleOffset.toInt() in item.offset..item.offset + item.size && currentDragItem.index != item.index
                    }
                    // Index check so Activities can´t be moved above days. For some reason targetItem.contentType is incorrect
                    if (targetItem != null && targetItem.index != 0) { //  && uiState.value.travels[targetItem.index] !is TravelType.Day
                        viewModel.moveActivity(
                            currentDragIndex, targetItem.index
                        )
                        viewModel.updateDraggableItemIndex(
                            targetItem.index
                        )
                        viewModel.updateDraggingItem(targetItem)
                        viewModel.updateDragAmountY(uiState.value.draggingItem!!.offset.toFloat() - targetItem.offset)

                    } else {
                        val startOffsetToTop =
                            startOffset - listState.layoutInfo.viewportStartOffset
                        val endOffsetToBottom = endOffset - listState.layoutInfo.viewportEndOffset
                        val scroll = when {
                            startOffsetToTop < 0 -> startOffsetToTop.coerceAtMost(
                                0f
                            )

                            endOffsetToBottom > 0 -> endOffsetToBottom.coerceAtLeast(
                                0f
                            )

                            else -> 0f
                        }
                        val canScrollDown =
                            uiState.value.draggingIndex != uiState.value.travels.size - 1 && endOffsetToBottom > 0
                        val canScrollUp = uiState.value.draggingIndex != 0 && startOffsetToTop < 0
                        if (scroll != 0f && (canScrollUp || canScrollDown)) {
                            scrollChannel.trySend(scroll)
                        }
                    }

                })
            }, imageVector = Icons.Default.DragIndicator, contentDescription = null
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateTravelTopBar(
    modifier: Modifier = Modifier, viewModel: CreateTravelViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    TravelAppBar(
        modifier = modifier,
        title = stringResource(R.string.create_travel_title),
        actions = {
            if (!uiState.value.isEditing) {
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
                                Text(text = stringResource(R.string.create_travel_edit))
                            }, onClick = {
                                viewModel.setIsEditing(true)
                                viewModel.setDropdownMenuShowing(false)
                            })
                            DropdownMenuItem(text = {
                                Text(text = stringResource(R.string.create_travel_add_date))
                            }, onClick = {
                                viewModel.setDropdownMenuShowing(false)
                            })

                            DropdownMenuItem(text = {
                                Text(text = stringResource(R.string.create_travel_delete_travel))
                            }, onClick = {
                                viewModel.showDeleteDialog()
                            })
                        }
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                if (uiState.value.isEditing) {
                    viewModel.setIsEditing(false)
                } else {
                    viewModel.createTravel()
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null
                )
            }

        })
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateTravelScaffold(
    modifier: Modifier = Modifier, content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier, topBar = { CreateTravelTopBar() }) { innerPadding ->
        content(innerPadding)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun CreateTravelScreenPreview(modifier: Modifier = Modifier) {
    CreateTravelScreen(onTravelDeleted = {}, onTravelCreated = {})
}