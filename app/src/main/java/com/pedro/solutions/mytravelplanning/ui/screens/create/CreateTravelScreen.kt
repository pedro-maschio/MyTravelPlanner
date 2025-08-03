package com.pedro.solutions.mytravelplanning.ui.screens.create

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pedro.solutions.mytravelplanning.R
import com.pedro.solutions.mytravelplanning.data.models.TravelType
import com.pedro.solutions.mytravelplanning.ui.screens.commons.LoadingState
import com.pedro.solutions.mytravelplanning.ui.screens.commons.TravelButton
import com.pedro.solutions.mytravelplanning.ui.screens.commons.TravelTextField
import com.pedro.solutions.mytravelplanning.ui.theme.Typography
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenOne
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenSix
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenTwo
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateTravelScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateTravelViewModel = koinViewModel(),
    travelId: Long? = null,
    deleteTravel: Boolean = false,
    createTravel: Boolean = false,
    isEditing: Boolean = false,
    onFinishEditing: () -> Unit,
    onTravelCreated: () -> Unit,
    onTravelDeleted: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val focusManager = LocalFocusManager.current

    BackHandler {
        // TODO: fix the focus from the keyboard, when the user clicks back
        focusManager.clearFocus()
        if (isEditing) {
            onFinishEditing()
        }
        viewModel.createTravel()
        onTravelCreated()
        backDispatcher?.onBackPressed()
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest {
            onTravelDeleted()
        }
    }

    LaunchedEffect(createTravel) {
        if(createTravel) {
            viewModel.createTravel()
            onTravelCreated()
        }
    }

    LaunchedEffect(deleteTravel) {
        viewModel.deleteTravel(deleteTravel)
    }

    LaunchedEffect(Unit) {
        viewModel.loadTravel(travelId)
        viewModel.setEditingState(isEditing)
        viewModel.addDefaultDay()
    }

    if (uiState.value.isLoading) {
        LoadingState(isLoading = true)
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(DimenTwo),
            verticalArrangement = Arrangement.spacedBy(DimenOne),
            state = listState
        ) {
            item {
                TravelTextField(
                    modifier = Modifier.padding(bottom = DimenTwo),
                    value = uiState.value.travelName,
                    placeHolder = stringResource(R.string.create_travel_name_placeholder),
                    onValueChange = {
                        viewModel.updateTravelName(travelName = it)
                    }
                )
            }

            itemsIndexed(uiState.value.travels) { index, item ->
                Row(
                    modifier = Modifier.padding(bottom = DimenOne),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (item) {
                        is TravelType.Day -> {
                            TravelTextField(
                                modifier = Modifier
                                    .weight(1f),
                                value = item.title,
                                onValueChange = {
                                    viewModel.updateTravelDayText(index = item.index, newText = it)
                                }
                            )
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

                        is TravelType.Activity -> {
                            TravelTextField(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = DimenOne),
                                value = item.title,
                                onValueChange = {
                                    viewModel.updateTravelActivityText(
                                        dayIndex = item.dayIndex,
                                        activityIndex = item.index,
                                        newText = it
                                    )
                                }
                            )
                        }
                    }

                }
            }

            item {
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

@Preview
@Composable
fun CreateTravelScreenPreview(modifier: Modifier = Modifier) {
    CreateTravelScreen(onFinishEditing = {}, onTravelCreated = {}, onTravelDeleted = {})
}