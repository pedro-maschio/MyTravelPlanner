package com.pedro.solutions.mytravelplanning.ui.screens.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pedro.solutions.mytravelplanning.R
import com.pedro.solutions.mytravelplanning.ui.screens.commons.ConnectionError
import com.pedro.solutions.mytravelplanning.ui.screens.commons.LoadingState
import com.pedro.solutions.mytravelplanning.ui.theme.Typography
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenSize120
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenThree
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenTwo
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateTravelScreen(
    modifier: Modifier = Modifier,
    travelId: Int? = null,
    onTravelSaved: () -> Unit
) {

    val viewModel: CreateTravelViewModel = koinViewModel()
    val state = viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest {
            onTravelSaved()
        }
    }

    if (state.value.showErrorScreen) {
        ConnectionError {
            viewModel.createTravel()
        }
        return
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = DimenTwo, vertical = DimenTwo)
            .verticalScroll(rememberScrollState())
    ) {

        Column(modifier = Modifier.weight(1f)) {
            LoadingState(isLoading = state.value.isLoading)
            Text(
                style = Typography.titleLarge,
                text = stringResource(R.string.create_travel_message)
            )
            Spacer(modifier = Modifier.height(DimenThree))

            Text(text = stringResource(R.string.create_travel_starting_point_name))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(size = DimenTwo),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                value = state.value.travel.startingPoint.orEmpty(),
                onValueChange = {
                    viewModel.updateStartingPoint(it)
                })
            Spacer(modifier = Modifier.height(DimenTwo))

            Text(text = stringResource(R.string.create_travel_ending_point_name))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(size = DimenTwo),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                value = state.value.travel.endingPoint.orEmpty(),
                onValueChange = {
                    viewModel.updateEndingPoint(it)
                })
            Spacer(modifier = Modifier.height(DimenTwo))

            Text(text = stringResource(R.string.create_travel_duration_in_days))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(size = DimenTwo),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                value = state.value.travel.durationInDays,
                onValueChange = {
                    viewModel.updateDuration(it)
                })
            Spacer(modifier = Modifier.height(DimenTwo))
        }
        Button(modifier = Modifier.align(Alignment.End), onClick = {
            viewModel.createTravel()
        }) {
            Text(
                text = if (travelId != null) stringResource(R.string.edit_travel_button) else stringResource(
                    R.string.create_travel_button
                )
            )
        }
    }
}

@Preview
@Composable
fun CreateTravelScreenPreview(modifier: Modifier = Modifier) {
    CreateTravelScreen() {

    }
}