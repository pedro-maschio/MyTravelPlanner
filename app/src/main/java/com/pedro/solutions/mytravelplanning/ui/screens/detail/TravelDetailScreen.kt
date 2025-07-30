package com.pedro.solutions.mytravelplanning.ui.screens.detail

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.pedro.solutions.mytravelplanning.ui.theme.Typography
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenOne
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenThree
import kotlinx.coroutines.channels.Channel


@Composable
fun TravelDetailScreen(
    modifier: Modifier = Modifier, travelItems: List<TravelItem>
) {
    val scrollChannel = Channel<Float>()
    var mutableTravelItems = travelItems.toMutableList()
    val listState = rememberLazyListState()
    var delta: Float by remember { mutableFloatStateOf(0f) }
    var draggingItemIndex: Int? by remember {
        mutableStateOf(null)
    }
    var draggingItem: LazyListItemInfo? by remember {
        mutableStateOf(null)
    }
    val onMove = { fromIndex: Int, toIndex: Int ->
        mutableTravelItems =
            mutableTravelItems.apply { add(toIndex, removeAt(fromIndex)) }
    }

    LaunchedEffect(listState) {
        while (true) {
            val diff = scrollChannel.receive()
            listState.scrollBy(diff)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(DimenThree)
            .pointerInput(key1 = listState) {
                detectDragGesturesAfterLongPress(onDragStart = { offset ->
                    listState.layoutInfo.visibleItemsInfo.firstOrNull { item -> offset.y.toInt() in item.offset..(item.offset + item.size) }
                        ?.also {
                            (it.contentType as? TravelItem.Day)?.let { day ->
                                draggingItemIndex = day.index
                            }
                        }
                }, onDrag = { change, dragAmount ->
                    change.consume()
                    delta += dragAmount.y

                    val currentDraggingItemIndex =
                        draggingItemIndex ?: return@detectDragGesturesAfterLongPress

                    val currentDraggingItem =
                        draggingItem ?: return@detectDragGesturesAfterLongPress
                    val startOffset = currentDraggingItem.offset + delta
                    val endOffset = currentDraggingItem.offset + currentDraggingItem.size + delta
                    val middleOffset = startOffset + (endOffset - startOffset) / 2

                    val targetItem = listState.layoutInfo.visibleItemsInfo.find { item ->
                        middleOffset.toInt() in item.offset..item.offset + item.size && currentDraggingItem.index != item.index && item.contentType is TravelItem.Day
                    }
                    if(targetItem != null) {
                        val targetIndex = (targetItem.contentType as TravelItem.Day).index
                        Log.d("PEDRO123", "targetIndex=$targetIndex currentDraggingItemIndex=$currentDraggingItemIndex")
                        onMove(currentDraggingItemIndex, targetIndex)
                        draggingItemIndex = targetIndex
                        draggingItem = targetItem
                        delta += currentDraggingItem.offset - targetItem.offset
                    } else {
                        val startOffsetToTop =
                            startOffset - listState.layoutInfo.viewportStartOffset
                        val endOffsetToBottom =
                            endOffset - listState.layoutInfo.viewportEndOffset
                        val scroll =
                            when {
                                startOffsetToTop < 0 -> startOffsetToTop.coerceAtMost(0f)
                                endOffsetToBottom > 0 -> endOffsetToBottom.coerceAtLeast(0f)
                                else -> 0f
                            }
                        val canScrollDown = currentDraggingItemIndex != mutableTravelItems.size - 1 && endOffsetToBottom > 0
                        val canScrollUp = currentDraggingItemIndex != 0 && startOffsetToTop < 0
                        if (scroll != 0f && (canScrollUp || canScrollDown)) {
                            scrollChannel.trySend(scroll)
                        }
                    }
                }, onDragEnd = {
                    draggingItem = null
                    draggingItemIndex = null
                    delta = 0f
                }, onDragCancel = {
                    draggingItem = null
                    draggingItemIndex = null
                    delta = 0f
                })
            }, state = listState
    ) {
        itemsIndexed(
            mutableTravelItems,
            contentType = { index, _ -> TravelItem.Day(index = index, "") }) { index, item ->

            val itemModifier = if (draggingItemIndex == index) {
                Modifier
                    .zIndex(1f)
                    .graphicsLayer {
                        translationY = delta
                    }
            } else {
                Modifier
            }
            when (item) {
                is TravelItem.Day -> TravelDay(
                    modifier = itemModifier,
                    title = item.title
                )

                is TravelItem.Activity -> TravelActivity(
                    modifier = itemModifier,
                    title = item.title
                )
            }
        }
    }
}

@Composable
fun TravelDay(modifier: Modifier = Modifier, title: String) {
    Column {
        Text(text = title, style = Typography.titleMedium)
    }
}

@Composable
fun TravelActivity(modifier: Modifier = Modifier, title: String) {
    Column(modifier = modifier.padding(bottom = DimenOne)) {
        Text(text = title, style = Typography.bodyMedium)
    }
}

@Preview
@Composable
fun TravelDetailScreenPreview(modifier: Modifier = Modifier) {
    val travelItems = listOf(
        TravelItem.Day(index = 0, title = "Dia 1: Chegada e Centro Histórico"),
        TravelItem.Activity("Saída de Brasília cedo para chegar antes do almoço."),
        TravelItem.Activity("Check-in em uma pousada charmosa, como Pousada dos Pirineus ou Casarão Villa do Império."),
        TravelItem.Day(index = 1, title = "Dia 2: Cachoeiras e Serra dos Pireneus"),
        TravelItem.Activity("Café da manhã reforçado na pousada."),
        TravelItem.Activity("Visita à Cachoeira do Abade (trilha leve, ótima para banho)"),
        TravelItem.Activity("Alternativa: Cachoeira Meia Lua (mais próxima do centro)."),
        TravelItem.Activity("Restaurante Fazenda Babilônia (famoso por seu café colonial e comida típica)."),
        TravelItem.Day(index = 2, title = "Dia 3: Últimas Cachoeiras e Despedida"),
        TravelItem.Activity("Café da manhã e checkout da pousada."),
        TravelItem.Activity("Restaurante Central (pratos regionais fartos) ou retorno ao \"Dona Cida\" se quiser repetir a experiência."),
        TravelItem.Activity("Visita à Cachoeira Bonsucesso (conjunto de quedas d’água com trilhas fáceis)."),
        TravelItem.Activity("Restaurante Central (pratos regionais fartos) ou retorno ao \"Dona Cida\" se quiser repetir a experiência."),
        TravelItem.Activity("Caminhada leve pelo centro para últimas compras (artesanato, licores e doces)."),
        TravelItem.Day(index = 3, title = "Dia 1: Chegada e Centro Histórico"),
        TravelItem.Activity("Saída de Brasília cedo para chegar antes do almoço."),
        TravelItem.Activity("Check-in em uma pousada charmosa, como Pousada dos Pirineus ou Casarão Villa do Império."),
        TravelItem.Day(index = 4, title = "Dia 2: Cachoeiras e Serra dos Pireneus"),
        TravelItem.Activity("Café da manhã reforçado na pousada."),
        TravelItem.Activity("Visita à Cachoeira do Abade (trilha leve, ótima para banho)"),
        TravelItem.Activity("Alternativa: Cachoeira Meia Lua (mais próxima do centro)."),
        TravelItem.Activity("Restaurante Fazenda Babilônia (famoso por seu café colonial e comida típica)."),
        TravelItem.Day(index = 5, title = "Dia 3: Últimas Cachoeiras e Despedida"),
        TravelItem.Activity("Café da manhã e checkout da pousada."),
        TravelItem.Activity("Restaurante Central (pratos regionais fartos) ou retorno ao \"Dona Cida\" se quiser repetir a experiência."),
        TravelItem.Activity("Visita à Cachoeira Bonsucesso (conjunto de quedas d’água com trilhas fáceis)."),
        TravelItem.Activity("Restaurante Central (pratos regionais fartos) ou retorno ao \"Dona Cida\" se quiser repetir a experiência."),
        TravelItem.Activity("Caminhada leve pelo centro para últimas compras (artesanato, licores e doces)."),
    )

    TravelDetailScreen(travelItems = travelItems)
}