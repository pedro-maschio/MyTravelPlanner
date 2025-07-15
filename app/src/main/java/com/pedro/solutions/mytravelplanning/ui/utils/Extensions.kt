package com.pedro.solutions.mytravelplanning.ui.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest

suspend fun <T> Flow<T>.collectLatestSafety(
    action: suspend (T) -> Unit
) {
    this
        .catch { }
        .collectLatest { action(it) }
}