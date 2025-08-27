package com.pedro.solutions.mytravelplanning.ui.screens.create

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pedro.solutions.mytravelplanning.R
import com.pedro.solutions.mytravelplanning.data.models.TravelType
import com.pedro.solutions.mytravelplanning.ui.components.LoadingState
import com.pedro.solutions.mytravelplanning.ui.components.TravelAppBar
import com.pedro.solutions.mytravelplanning.ui.components.TravelButton
import com.pedro.solutions.mytravelplanning.ui.components.TravelDateCard
import com.pedro.solutions.mytravelplanning.ui.components.TravelDialog
import com.pedro.solutions.mytravelplanning.ui.components.TravelTextField
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenHalf
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenOne
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenSix
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenTwo
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
                                Text(
                                    text = if (uiState.value.hasTravelDates) {
                                        stringResource(R.string.create_travel_edit_date)
                                    } else {
                                        stringResource(R.string.create_travel_add_date)
                                    }
                                )
                            }, onClick = {
                                viewModel.setDropdownMenuShowing(false)
                                viewModel.showDatePickerModal()
                            })

                            DropdownMenuItem(text = {
                                Text(stringResource(R.string.create_travel_see_details))
                            }, onClick = {
                                viewModel.setDropdownMenuShowing(false)
                                viewModel.showDetailsAlertDialog()
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

@OptIn(ExperimentalMaterial3Api::class)
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
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

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
                is CreateTravelUiEvent.OnDayAdded -> viewModel.onDayAdded(
                    dayTitle = context.getString(
                        R.string.create_travel_day_placeholder
                    )
                )
            }

        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadTravel(travelId)
        viewModel.setEditingState(isEditing)
        viewModel.addDay()
    }

    if (uiState.value.isDeleteDialogShowing) {
        TravelDialog(
            title = stringResource(R.string.create_travel_delete_dialog_title),
            message = {
                Text(
                    text = stringResource(
                        R.string.create_travel_delete_dialog_message,
                    )
                )
            },
            onDismiss = {
                viewModel.hideDeleteDialog()
            },
            onConfirm = {
                viewModel.deleteTravel()
            },
            onCancel = {
                viewModel.hideDeleteDialog()
            })
    }

    if (uiState.value.isDetailsAlertDialogShowing) {
        TravelDialog(
            title = stringResource(R.string.create_travel_details_dialog_title),
            message = {
                Column {
                    if (uiState.value.hasTravelDates) {
                        Text(
                            text = stringResource(
                                R.string.create_travel_details_dialog_message_dates,
                                uiState.value.formattedCreatedAt,
                                uiState.value.formattedUpdatedAt,
                            )
                        )
                    }
                    Text(
                        text = stringResource(
                            R.string.create_travel_details_dialog_message_days,
                            uiState.value.travel.days.size
                        )
                    )
                }
            },
            confirmButtonText = stringResource(R.string.create_travel_details_dialog_close),
            onDismiss = {
                viewModel.hideDetailsAlertDialog()
            },
            onConfirm = {
                viewModel.hideDetailsAlertDialog()
            })
    }


    @Composable
    fun TravelDay(modifier: Modifier = Modifier, item: TravelType.Day) {
        TravelTextField(
            modifier = modifier,
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

    @Composable
    fun DateRangePickerModal(
        startDate: String,
        endDate: String,
        onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
        onDismiss: () -> Unit
    ) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC") // prevents timezone shifts
        var start: Date? = null
        var end: Date? = null
        if (uiState.value.hasTravelDates) {
            start = sdf.parse(startDate)
            end = sdf.parse(endDate)
        }

        val dateRangePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = start?.time,
            initialSelectedEndDateMillis = end?.time
        )
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateRangeSelected(
                            Pair(
                                dateRangePickerState.selectedStartDateMillis,
                                dateRangePickerState.selectedEndDateMillis
                            )
                        )
                        onDismiss()
                    }
                ) {
                    Text(text = stringResource(R.string.create_travel_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.create_travel_cancel))
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = {
                    Text(text = stringResource(R.string.create_travel_select_date_range))
                },
                showModeToggle = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(16.dp)
            )
        }
    }

    CreateTravelScaffold { innerPadding ->
        if (uiState.value.isDatePickerShowing) {
            DateRangePickerModal(
                startDate = uiState.value.travel.formattedStartDate,
                endDate = uiState.value.travel.formattedEndDate,
                onDateRangeSelected = {
                    viewModel.onDateRangeSelected(it)
                },
                onDismiss = {
                    viewModel.hideDatePickerModal()
                })
        }

        if (uiState.value.isLoading) {
            LoadingState(isLoading = true)
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = DimenTwo),
                verticalArrangement = Arrangement.spacedBy(DimenOne),
            ) {
                item {
                    if (uiState.value.hasTravelDates) {
                        TravelDateCard(
                            startDate = uiState.value.travel.formattedStartDate,
                            endDate = uiState.value.travel.formattedEndDate
                        ) {
                            viewModel.showDatePickerModal()
                        }
                    }
                }
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
                    items = uiState.value.travels
                ) { index, item ->
                    Row(
                        modifier = Modifier.padding(vertical = DimenHalf),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (item) {
                            is TravelType.Day -> TravelDay(
                                modifier = Modifier.weight(1f),
                                item = item
                            )

                            is TravelType.Activity ->
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
@Preview
@Composable
fun CreateTravelScreenPreview(modifier: Modifier = Modifier) {
    CreateTravelScreen(onTravelDeleted = {}, onTravelCreated = {})
}