package com.sotti.milliscope.data

import android.os.SystemClock
import com.sotti.milliscope.model.ElapsedRealTimeWhenBecameVisible
import com.sotti.milliscope.model.ItemId
import com.sotti.milliscope.model.MainActivityState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

internal fun MutableStateFlow<MainActivityState>.updateVisibleItems(
    visibleItems: Map<ItemId, ElapsedRealTimeWhenBecameVisible>,
) {
    update { state ->
        val now = SystemClock.elapsedRealtime()
        state.copy(
            items = state.items.map { item ->
                visibleItems[item.id]?.let { start ->
                    val total =
                        item.previouslyAccumulatedVisibleTimeInMilliSeconds + (now - start.value)
                    item.copy(
                        visibleTimeInMilliSeconds = total,
                        formattedVisibleTimeInSeconds = total.toVisibleTime(),
                    )
                } ?: item
            }
        )
    }
}

internal fun MutableStateFlow<MainActivityState>.updateNotVisibleItems(
    elapsedRealTimeWhenBecameVisible: ElapsedRealTimeWhenBecameVisible,
    itemId: ItemId,
) {
    update { state ->
        val now = SystemClock.elapsedRealtime()
        val timeSinceBecameVisible = now - elapsedRealTimeWhenBecameVisible.value
        state.copy(
            items = state.items.map { item ->
                if (item.id == itemId) {
                    val total =
                        item.previouslyAccumulatedVisibleTimeInMilliSeconds + timeSinceBecameVisible
                    item.copy(
                        previouslyAccumulatedVisibleTimeInMilliSeconds = total,
                        visibleTimeInMilliSeconds = total,
                        formattedVisibleTimeInSeconds = total.toVisibleTime(),
                    )
                } else item
            }
        )
    }
}

private fun Long.toVisibleTime(): String =
    String.format(Locale.getDefault(), "%.1f", this / 1000f)
