package com.pedro.solutions.mytravelplanning.ui.screens.generate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pedro.solutions.mytravelplanning.R
import com.pedro.solutions.mytravelplanning.data.models.openai.TravelGuide
import com.pedro.solutions.mytravelplanning.ui.components.ConnectionError
import com.pedro.solutions.mytravelplanning.ui.components.LoadingState
import com.pedro.solutions.mytravelplanning.ui.components.TravelAppBar
import com.pedro.solutions.mytravelplanning.ui.components.TravelButton
import com.pedro.solutions.mytravelplanning.ui.components.TravelTextField
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenTwo
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun GenerateTravelScreen(
    modifier: Modifier = Modifier, travelId: Int? = null, onTravelGenerated: (TravelGuide) -> Unit
) {

    val viewModel: GenerateTravelViewModel = koinViewModel()
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest {
            when (it) {
                is GenerateTravelEvents.GoToListing -> onTravelGenerated(it.travelGuide)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TravelAppBar(
                title = stringResource(
                    R.string.generate_travel_title
                )
            )
        }) { innerPadding ->

        if (state.value.showErrorScreen || state.value.isLoading) {
            ConnectionError(
                modifier = Modifier.padding(innerPadding),
                isShowing = state.value.showErrorScreen
            ) {
                viewModel.generateTravel()
            }
            LoadingState(
                modifier = Modifier.padding(innerPadding),
                isLoading = state.value.isLoading
            )
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(R.string.generate_travel_starting_point_name))
                    TravelTextField(
                        value = state.value.travel.startingPoint.orEmpty(),
                        onValueChange = {
                            viewModel.updateStartingPoint(it)
                        }
                    )
                    Spacer(modifier = Modifier.height(DimenTwo))

                    Text(text = stringResource(R.string.generate_travel_ending_point_name))
                    TravelTextField(
                        value = state.value.travel.endingPoint.orEmpty(),
                        onValueChange = {
                            viewModel.updateEndingPoint(it)
                        }
                    )
                    Spacer(modifier = Modifier.height(DimenTwo))

                    Text(text = stringResource(R.string.generate_travel_duration_in_days))


                    Box(modifier = Modifier.clickable {
                        viewModel.showDropDown()
                    }) {
                        TravelTextField(
                            value = state.value.travel.durationInDays,
                            colors = TextFieldDefaults.colors(
                                disabledTextColor = Color.White,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            enabled = false,
                            trailingIcon = {
                                Icon(Icons.Filled.ArrowDropDown, null)
                            }
                        )
                    }

                    Box {
                        DropdownMenu(
                            expanded = state.value.isDropDownMenuShowing,
                            onDismissRequest = {
                                viewModel.hideDropDown()
                            }) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        pluralStringResource(
                                            R.plurals.generate_travel_days,
                                            1,
                                            1
                                        )
                                    )
                                },
                                onClick = {
                                    viewModel.updateDuration("1")
                                })
                            DropdownMenuItem(text = {
                                Text(
                                    pluralStringResource(
                                        R.plurals.generate_travel_days,
                                        2,
                                        2
                                    )
                                )
                            }, onClick = {
                                viewModel.updateDuration("2")
                            })
                            DropdownMenuItem(text = {
                                Text(
                                    pluralStringResource(
                                        R.plurals.generate_travel_days,
                                        7,
                                        7
                                    )
                                )
                            }, onClick = {
                                viewModel.updateDuration("7")
                            })
                            DropdownMenuItem(text = {
                                Text(
                                    pluralStringResource(
                                        R.plurals.generate_travel_days,
                                        14,
                                        14
                                    )
                                )
                            }, onClick = {
                                viewModel.updateDuration("14")
                            })
                        }
                    }

                    Spacer(modifier = Modifier.height(DimenTwo))

                    Text(text = state.value.errorMessage)
                }
                TravelButton(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        viewModel.generateTravel()
                    },
                    text = if (travelId != null) stringResource(R.string.edit_travel_button) else stringResource(
                        R.string.generate_travel_button
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun GenerateTravelScreenPreview(modifier: Modifier = Modifier) {
    GenerateTravelScreen() {

    }
}