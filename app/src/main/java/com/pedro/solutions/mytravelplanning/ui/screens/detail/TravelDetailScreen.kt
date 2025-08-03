package com.pedro.solutions.mytravelplanning.ui.screens.detail

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pedro.solutions.mytravelplanning.data.models.TravelType
import com.pedro.solutions.mytravelplanning.ui.navigation.TravelsRoutes
import com.pedro.solutions.mytravelplanning.ui.theme.Typography
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenOne
import com.pedro.solutions.mytravelplanning.ui.utils.Dimens.DimenThree
import kotlinx.coroutines.channels.Channel
import org.koin.androidx.compose.koinViewModel


@Composable
fun TravelDetailScreen(
    modifier: Modifier = Modifier,
    travelData: TravelsRoutes.TravelDetailScreen,
    viewModel: TravelDetailViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadTravelDetail(travelData)
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val scrollChannel = Channel<Float>()
    var mutableTravelItems = uiState.value.travelItems.toMutableList()
    val listState = rememberLazyListState()


    var delta: Float by remember { mutableFloatStateOf(0f) }
    var draggingItemIndex: Int? by remember {
        mutableStateOf(null)
    }
    var draggingItem: LazyListItemInfo? by remember {
        mutableStateOf(null)
    }
    val onMove = { fromIndex: Int, toIndex: Int ->
        mutableTravelItems = mutableTravelItems.apply { add(toIndex, removeAt(fromIndex)) }
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
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        listState.layoutInfo.visibleItemsInfo.firstOrNull { item -> offset.y.toInt() in item.offset..(item.offset + item.size) }
                            ?.also {
                                (it.contentType as? TravelType.Day)?.let { day ->
                                    draggingItem = it
                                    draggingItemIndex = day.index
                                }
                            }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        delta += dragAmount.y
                        val currentDraggingItemIndex =
                            draggingItemIndex ?: return@detectDragGesturesAfterLongPress
                        val currentDraggingItem =
                            draggingItem ?: return@detectDragGesturesAfterLongPress

                        val startOffset = currentDraggingItem.offset + delta
                        val endOffset =
                            currentDraggingItem.offset + currentDraggingItem.size + delta
                        val middleOffset = startOffset + (endOffset - startOffset) / 2

                        val targetItem =
                            listState.layoutInfo.visibleItemsInfo.find { item ->
                                middleOffset.toInt() in item.offset..item.offset + item.size &&
                                        currentDraggingItem.index != item.index &&
                                        item.contentType is TravelType.Day
                            }
                        if (targetItem != null) {
                            val targetIndex = (targetItem.contentType as TravelType.Day).index
                            onMove(currentDraggingItemIndex, targetIndex)
                            draggingItemIndex = targetIndex
                            delta += currentDraggingItem.offset - targetItem.offset
                            draggingItem = targetItem
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
                            val canScrollDown =
                                currentDraggingItemIndex != mutableTravelItems.size - 1 && endOffsetToBottom > 0
                            val canScrollUp = currentDraggingItemIndex != 0 && startOffsetToTop < 0
                            if (scroll != 0f && (canScrollUp || canScrollDown)) {
                                scrollChannel.trySend(scroll)
                            }
                        }
                    },
                    onDragEnd = {
                        draggingItem = null
                        draggingItemIndex = null
                        delta = 0f
                    },
                    onDragCancel = {
                        draggingItem = null
                        draggingItemIndex = null
                        delta = 0f
                    },
                )
            }, state = listState
    ) {
        itemsIndexed(
            mutableTravelItems,
            contentType = { index, _ -> TravelType.Day(index, "") }) { index, item ->

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
                is TravelType.Day -> TravelDay(
                    modifier = itemModifier, title = item.title
                )

                is TravelType.Activity -> TravelActivity(
                    modifier = itemModifier, title = item.title
                )
            }
        }
    }
}

@Composable
fun TravelDay(modifier: Modifier = Modifier, title: String) {
    ElevatedCard(modifier = modifier.padding(vertical = DimenOne)) {
        Text(
            modifier = Modifier.padding(DimenOne),
            text = title,
            style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun TravelActivity(modifier: Modifier = Modifier, title: String) {
    ElevatedCard(modifier = modifier.padding(vertical = DimenOne)) {
        Text(modifier = Modifier.padding(DimenOne), text = title, style = Typography.bodyLarge)
    }
}

@Preview
@Composable
fun TravelDetailScreenPreview(modifier: Modifier = Modifier) {
//    val travelItems = listOf(
//        TravelType.Day(index = 0, title = "Dia 1: Chegada e Centro Histórico"),
//        TravelType.Activity(dayIndex = 0, "Saída de Brasília cedo para chegar antes do almoço."),
//        TravelType.Activity(
//            dayIndex = 0,
//            "Check-in em uma pousada charmosa, como Pousada dos Pirineus ou Casarão Villa do Império."
//        ),
//        TravelType.Day(index = 1, title = "Dia 2: Cachoeiras e Serra dos Pireneus"),
//        TravelType.Activity(dayIndex = 1, "Café da manhã reforçado na pousada."),
//        TravelType.Activity(
//            dayIndex = 1,
//            "Visita à Cachoeira do Abade (trilha leve, ótima para banho)"
//        ),
//        TravelType.Activity(
//            dayIndex = 1,
//            "Alternativa: Cachoeira Meia Lua (mais próxima do centro)."
//        ),
//        TravelType.Activity(
//            dayIndex = 1,
//            "Restaurante Fazenda Babilônia (famoso por seu café colonial e comida típica)."
//        ),
//        TravelType.Day(index = 2, title = "Dia 3: Últimas Cachoeiras e Despedida"),
//        TravelType.Activity(dayIndex = 2, "Café da manhã e checkout da pousada."),
//        TravelType.Activity(
//            dayIndex = 2,
//            "Restaurante Central (pratos regionais fartos) ou retorno ao \"Dona Cida\" se quiser repetir a experiência."
//        ),
//        TravelType.Activity(
//            dayIndex = 2,
//            "Visita à Cachoeira Bonsucesso (conjunto de quedas d’água com trilhas fáceis)."
//        ),
//        TravelType.Activity(
//            dayIndex = 2,
//            "Restaurante Central (pratos regionais fartos) ou retorno ao \"Dona Cida\" se quiser repetir a experiência."
//        ),
//        TravelType.Activity(
//            dayIndex = 2,
//            "Caminhada leve pelo centro para últimas compras (artesanato, licores e doces)."
//        ),
//        TravelType.Day(index = 3, title = "Dia 1: Chegada e Centro Histórico"),
//        TravelType.Activity(dayIndex = 3, "Saída de Brasília cedo para chegar antes do almoço."),
//        TravelType.Activity(
//            dayIndex = 3,
//            "Check-in em uma pousada charmosa, como Pousada dos Pirineus ou Casarão Villa do Império."
//        ),
//        TravelType.Day(index = 4, title = "Dia 2: Cachoeiras e Serra dos Pireneus"),
//        TravelType.Activity(dayIndex = 4, "Café da manhã reforçado na pousada."),
//        TravelType.Activity(
//            dayIndex = 4,
//            "Visita à Cachoeira do Abade (trilha leve, ótima para banho)"
//        ),
//        TravelType.Activity(
//            dayIndex = 4,
//            "Alternativa: Cachoeira Meia Lua (mais próxima do centro)."
//        ),
//        TravelType.Activity(
//            dayIndex = 4,
//            "Restaurante Fazenda Babilônia (famoso por seu café colonial e comida típica)."
//        ),
//        TravelType.Day(index = 5, title = "Dia 3: Últimas Cachoeiras e Despedida"),
//        TravelType.Activity(dayIndex = 5, "Café da manhã e checkout da pousada."),
//        TravelType.Activity(
//            dayIndex = 5,
//            "Restaurante Central (pratos regionais fartos) ou retorno ao \"Dona Cida\" se quiser repetir a experiência."
//        ),
//        TravelType.Activity(
//            dayIndex = 5,
//            "Visita à Cachoeira Bonsucesso (conjunto de quedas d’água com trilhas fáceis)."
//        ),
//        TravelType.Activity(
//            dayIndex = 5,
//            "Restaurante Central (pratos regionais fartos) ou retorno ao \"Dona Cida\" se quiser repetir a experiência."
//        ),
//        TravelType.Activity(
//            dayIndex = 5,
//            "Caminhada leve pelo centro para últimas compras (artesanato, licores e doces)."
//        )
//    )
//
//    TravelDetailScreen(travelItems = travelItems)
}