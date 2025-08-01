package com.pedro.solutions.mytravelplanning.ui.screens.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pedro.solutions.mytravelplanning.R
import com.pedro.solutions.mytravelplanning.data.models.TravelType
import com.pedro.solutions.mytravelplanning.ui.screens.commons.TravelButton
import com.pedro.solutions.mytravelplanning.ui.screens.commons.TravelTextField
import com.pedro.solutions.mytravelplanning.ui.theme.Typography
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenOne
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenTwo
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateTravelScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateTravelViewModel = koinViewModel(),
    onTravelCreated: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.addDefaultDay()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(DimenTwo),
        verticalArrangement = Arrangement.spacedBy(DimenOne)
    ) {
        item {
            Text(
                modifier = Modifier.padding(bottom = DimenTwo),
                style = Typography.titleLarge,
                text = stringResource(R.string.create_travel_title)
            )
        }

        itemsIndexed(uiState.value.travels) { index, item ->
            Row(
                modifier = Modifier.padding(bottom = if (index == uiState.value.travel.days.lastIndex) DimenTwo else DimenOne),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when(item) {
                    is TravelType.Day -> {
                        TravelTextField(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = DimenOne),
                            value = item?.title.orEmpty()
                        )
                        Icon(
                            modifier = Modifier
                                .padding(end = DimenOne)
                                .clickable {
                                    viewModel.addActivity(item.index)
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
                            value = item?.title.orEmpty()
                        )
                    }
                }

            }
        }

        item {
            TravelButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.create_travel_add_new_day)
            ) {
                viewModel.addDay()
            }
        }
    }

}

@Preview
@Composable
fun CreateTravelScreenPreview(modifier: Modifier = Modifier) {
    CreateTravelScreen {

    }
}