package com.pedro.solutions.mytravelplanning.ui.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedro.solutions.mytravelplanning.R
import com.pedro.solutions.mytravelplanning.ui.screens.main.MainScreenTravel
import com.pedro.solutions.mytravelplanning.ui.theme.Typography
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenOne
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenTwo

@Composable
fun TravelCard(
    travel: MainScreenTravel, onLongClick: () -> Unit, onClick: () -> Unit
) {
    val containerColor =
        if (travel.isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    OutlinedCard(
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
            .padding(all = DimenTwo)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = DimenOne),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = travel.travelName.ifBlank { stringResource(R.string.travels_listing_unnamed_travel) },
                style = Typography.titleMedium
            )
        }
    }
}

@Preview
@Composable
fun TravelCardPreview(modifier: Modifier = Modifier) {
    TravelCard(
        travel = MainScreenTravel(
            travelName = "Viagem para a disney", travelId = -1, isSelected = false
        ), onLongClick = { }) { }
}