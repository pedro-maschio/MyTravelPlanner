package com.pedro.solutions.mytravelplanning.ui.screens.intro

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pedro.solutions.mytravelplanning.R
import com.pedro.solutions.mytravelplanning.ui.screens.commons.TravelButton
import com.pedro.solutions.mytravelplanning.ui.theme.TravelsColors.PurpleGrey80
import com.pedro.solutions.mytravelplanning.ui.theme.Typography
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun IntroScreen(
    modifier: Modifier = Modifier,
    viewModel: IntroViewModel = koinViewModel(),
    goToNextScreen: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collectLatest {
            when (it) {
                IntroUiEvent.SaveSelectedVehicle -> {
                    goToNextScreen()
                }

                null -> {}
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,

        ) {
        Text(
            text = stringResource(R.string.intro_title),
            style = Typography.titleLarge,
            textAlign = TextAlign.Center
        )
        VehicleCard(
            vehicleImage = R.drawable.motorcicle, vehicleLabel = R.string.intro_motorcicle_choice,
            isHighLighted = uiState.value.selectedOption == SelectedOption.MOTORCYCLE
        ) {
            viewModel.onSelectedOptionChanged(SelectedOption.MOTORCYCLE)
        }
        VehicleCard(
            vehicleImage = R.drawable.car, vehicleLabel = R.string.intro_car_choice,
            isHighLighted = uiState.value.selectedOption == SelectedOption.CAR
        ) {
            viewModel.onSelectedOptionChanged(SelectedOption.CAR)
        }

        TravelButton(enabled = uiState.value.isSaveButtonShowing, onClick = {
            viewModel.onSaveSelectedVehicle()
        }, text = stringResource(R.string.intro_save_button))

        Text(
            text = stringResource(R.string.intro_info_text),
            style = Typography.labelSmall,
            textAlign = TextAlign.Center
        )

    }
}

@Composable
fun VehicleCard(
    modifier: Modifier = Modifier,
    @DrawableRes vehicleImage: Int,
    @StringRes vehicleLabel: Int,
    isHighLighted: Boolean = false,
    onClick: () -> Unit = {}
) {
    val cardColor = if (isHighLighted) {
        PurpleGrey80
    } else {
        CardDefaults.cardColors().containerColor
    }
    ElevatedCard(
        modifier = modifier
            .size(200.dp)
            .clickable { onClick.invoke() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (imageRef, labelRef) = createRefs()
            IconButton(
                modifier = Modifier
                    .size(50.dp)
                    .constrainAs(imageRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(labelRef.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }, onClick = {}) {
                Icon(
                    modifier = Modifier.size(50.dp),
                    painter = painterResource(vehicleImage),
                    contentDescription = null
                )
            }
            Text(
                modifier = Modifier.constrainAs(labelRef) {
                    top.linkTo(imageRef.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(labelRef.end)
                    end.linkTo(parent.end)
                }, text = stringResource(vehicleLabel), style = Typography.titleLarge
            )
        }
    }

}

@Preview
@Composable
fun IntroScreenPreview(modifier: Modifier = Modifier) {
    IntroScreen {

    }
}

@Preview
@Composable
fun VehicleCardPreview(modifier: Modifier = Modifier) {
    VehicleCard(vehicleImage = R.drawable.car, vehicleLabel = R.string.intro_car_choice)
}

