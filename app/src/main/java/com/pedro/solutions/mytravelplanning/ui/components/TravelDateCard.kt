package com.pedro.solutions.mytravelplanning.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedro.solutions.mytravelplanning.R
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenOne
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenTwo

@Composable
fun TravelDateCard(
    modifier: Modifier = Modifier,
    startDate: String,
    endDate: String,
    onClick: () -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(DimenOne)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DimenTwo),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = stringResource(R.string.create_travel_start_date),
                    color = Color.Gray
                )
                Text(
                    text = startDate,
                    fontWeight = FontWeight.Bold
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowForward,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(R.string.create_travel_end_date),
                    color = Color.Gray
                )
                Text(
                    text = endDate,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
fun TravelDateCardPreview(modifier: Modifier = Modifier) {
    TravelDateCard(startDate = "01/09/2025", endDate = "10/09/2025")
}