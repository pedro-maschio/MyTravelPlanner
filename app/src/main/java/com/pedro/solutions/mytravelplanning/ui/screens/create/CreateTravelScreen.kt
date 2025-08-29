package com.pedro.solutions.mytravelplanning.ui.screens.create

import android.content.Intent
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateTravelTopBar(
    modifier: Modifier = Modifier,
    uiState: CreateTravelUiState,
    onEvent: (CreateTravelUiEvent) -> Unit
) {
    val context = LocalContext.current
    TravelAppBar(
        modifier = modifier,
        title = stringResource(R.string.create_travel_title),
        actions = {
            if (!uiState.isEditing) {
                IconButton(onClick = {
                    onEvent(CreateTravelUiEvent.SetDropdownMenuShowing(true))
                }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert, contentDescription = null
                    )
                    Box(contentAlignment = Alignment.TopEnd) {
                        DropdownMenu(
                            expanded = uiState.isDropDownMenuShowing, onDismissRequest = {
                                onEvent(CreateTravelUiEvent.SetDropdownMenuShowing(false))
                            }) {
                            DropdownMenuItem(text = {
                                Text(text = stringResource(R.string.create_travel_edit))
                            }, onClick = {
                                onEvent(CreateTravelUiEvent.SetIsEditing(true))
                                onEvent(CreateTravelUiEvent.SetDropdownMenuShowing(false))
                            })
                            DropdownMenuItem(text = {
                                Text(
                                    text = if (uiState.hasTravelDates) {
                                        stringResource(R.string.create_travel_edit_date)
                                    } else {
                                        stringResource(R.string.create_travel_add_date)
                                    }
                                )
                            }, onClick = {
                                onEvent(CreateTravelUiEvent.SetDropdownMenuShowing(false))
                                onEvent(CreateTravelUiEvent.SetDatePickerShowing(true))
                            })

                            DropdownMenuItem(text = {
                                Text(stringResource(R.string.create_travel_see_details))
                            }, onClick = {
                                onEvent(CreateTravelUiEvent.SetDropdownMenuShowing(false))
                                onEvent(CreateTravelUiEvent.SetDetailsAlertDialogShowing(true))
                            })

                            DropdownMenuItem(text = {
                                Text(stringResource(R.string.create_travel_share_travel))
                            }, onClick = {
                                onEvent(CreateTravelUiEvent.ShareTravel)
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, uiState.shareTravelText)
                                    type = "text/plain"
                                }

                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)

                            })

                            DropdownMenuItem(text = {
                                Text(text = stringResource(R.string.create_travel_delete_travel))
                            }, onClick = {
                                onEvent(CreateTravelUiEvent.SetDeleteDialogShowing(true))
                            })
                        }
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                if (uiState.isEditing) {
                    onEvent(CreateTravelUiEvent.SetIsEditing(false))
                } else {
                    onEvent(CreateTravelUiEvent.OnTravelCreated)
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
    modifier: Modifier = Modifier,
    uiState: CreateTravelUiState,
    onEvent: (CreateTravelUiEvent) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = { CreateTravelTopBar(uiState = uiState, onEvent = onEvent) }) { innerPadding ->
        content(innerPadding)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateTravelScreen(
    modifier: Modifier = Modifier,
    uiState: CreateTravelUiState,
    onEvent: (CreateTravelUiEvent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    BackHandler {
        // TODO: fix the focus from the keyboard, when the user clicks back
        if (uiState.isEditing) {
            onEvent(CreateTravelUiEvent.SetIsEditing(false))
        } else {
            onEvent(CreateTravelUiEvent.OnTravelCreated)
        }
    }
    if (uiState.isDeleteDialogShowing) {
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
                onEvent(CreateTravelUiEvent.SetDeleteDialogShowing(false))
            },
            onConfirm = {
                onEvent(CreateTravelUiEvent.OnTravelDeleted)
            },
            onCancel = {
                onEvent(CreateTravelUiEvent.SetDeleteDialogShowing(false))
            })
    }

    if (uiState.isDetailsAlertDialogShowing) {
        TravelDialog(
            title = stringResource(R.string.create_travel_details_dialog_title),
            message = {
                Column {
                    if (uiState.hasTravelDates) {
                        Text(
                            text = stringResource(
                                R.string.create_travel_details_dialog_message_dates,
                                uiState.formattedCreatedAt,
                                uiState.formattedUpdatedAt,
                            )
                        )
                    }
                    Text(
                        text = stringResource(
                            R.string.create_travel_details_dialog_message_days,
                            uiState.travel.days.size
                        )
                    )
                }
            },
            confirmButtonText = stringResource(R.string.create_travel_details_dialog_close),
            onDismiss = {
                onEvent(CreateTravelUiEvent.SetDetailsAlertDialogShowing(false))
            },
            onConfirm = {
                onEvent(CreateTravelUiEvent.SetDetailsAlertDialogShowing(false))
            })
    }


    @Composable
    fun TravelDay(modifier: Modifier = Modifier, item: TravelType.Day) {
        TravelTextField(
            modifier = modifier,
            value = item.title,
            onValueChange = {
                onEvent(CreateTravelUiEvent.UpdateTravelDayText(item.index, it))
            },
            enabled = uiState.isEditing.not()
        )
        if (uiState.isEditing) {
            Icon(
                modifier = Modifier
                    .padding(horizontal = DimenOne)
                    .clickable {
                        onEvent(CreateTravelUiEvent.DeleteDay(item.index))
                    },
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        } else {
            Icon(
                modifier = Modifier
                    .padding(horizontal = DimenOne)
                    .clickable {
                        onEvent(CreateTravelUiEvent.AddActivity(item.index))
                        focusManager.clearFocus()
                    },
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
        }
    }

    @Composable
    fun DateRangePickerModal(
        uiState: CreateTravelUiState,
        startDate: String,
        endDate: String,
        onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
        onDismiss: () -> Unit
    ) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC") // prevents timezone shifts
        var start: Date? = null
        var end: Date? = null
        if (uiState.hasTravelDates) {
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

    CreateTravelScaffold(uiState = uiState, onEvent = onEvent) { innerPadding ->
        if (uiState.isDatePickerShowing) {
            DateRangePickerModal(
                uiState = uiState,
                startDate = uiState.travel.formattedStartDate,
                endDate = uiState.travel.formattedEndDate,
                onDateRangeSelected = {
                    onEvent(CreateTravelUiEvent.DateRangeSelected(it))
                },
                onDismiss = {
                    onEvent(CreateTravelUiEvent.SetDatePickerShowing(false))
                })
        }

        if (uiState.isLoading) {
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
                    if (uiState.hasTravelDates) {
                        TravelDateCard(
                            startDate = uiState.travel.formattedStartDate,
                            endDate = uiState.travel.formattedEndDate
                        ) {
                            onEvent(CreateTravelUiEvent.SetDatePickerShowing(true))
                        }
                    }
                }
                item {
                    TravelTextField(
                        modifier = Modifier.padding(vertical = DimenOne),
                        value = uiState.travelName,
                        placeHolder = stringResource(R.string.create_travel_name_placeholder),
                        onValueChange = {
                            onEvent(CreateTravelUiEvent.UpdateTravelName(it))
                        })
                }

                itemsIndexed(
                    items = uiState.travels
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
                                    enabled = uiState.isEditing.not(),
                                    onValueChange = {
                                        onEvent(
                                            CreateTravelUiEvent.UpdateTravelActivityText(
                                                dayIndex = item.dayIndex,
                                                activityIndex = item.index,
                                                newText = it
                                            )
                                        )
                                    })
                        }
                    }
                }

                item {
                    if (!uiState.isEditing) {
                        TravelButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(DimenSix)
                                .padding(bottom = DimenTwo),
                            text = stringResource(R.string.create_travel_add_new_day)
                        ) {
                            focusManager.clearFocus()
                            onEvent(
                                CreateTravelUiEvent.OnDayAdded(
                                    dayTitle = context.getString(
                                        R.string.create_travel_day_placeholder
                                    )
                                )
                            )
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
    CreateTravelScreen(
        modifier = modifier,
        uiState = CreateTravelUiState(
            travelName = "João Pessoa 2025",
            travels = listOf(
                TravelType.Day(0, "Dia 1"),
                TravelType.Activity(0, 0, "Ir para a praia"),
                TravelType.Day(0, "Dia 2"),
                TravelType.Activity(0, 0, "Comer em um bom restaurante")
            )
        ),
        onEvent = {})
}