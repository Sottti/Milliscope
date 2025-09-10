package com.sotti.milliscope.data

import com.sotti.milliscope.model.ElapsedRealTimeWhenBecameVisible
import com.sotti.milliscope.model.ItemId
import com.sotti.milliscope.model.MainActivityItemUi
import com.sotti.milliscope.model.MainActivityState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

internal fun MutableStateFlow<MainActivityState>.updateVisibleItems(
    visibleItems: Map<ItemId, ElapsedRealTimeWhenBecameVisible>,
    now: Long,
) {
    update { state ->
        state.copy(
            items = state.items.map { item ->
                item.updateItem(visibleItems = visibleItems, now = now)
            }
        )
    }
}

private fun MainActivityItemUi.updateItem(
    visibleItems: Map<ItemId, ElapsedRealTimeWhenBecameVisible>,
    now: Long,
): MainActivityItemUi {
    val start = visibleItems[id]?.value
    val total = when {
        start != null ->
            previouslyAccumulatedVisibleTimeInMilliSeconds + (now - start)

        else -> previouslyAccumulatedVisibleTimeInMilliSeconds
    }

    return when (total) {
        visibleTimeInMilliSeconds -> this
        else -> copy(
            formattedVisibleTimeInSeconds = total.toVisibleTime(),
            visibleTimeInMilliSeconds = total,
        )
    }
}

internal fun MutableStateFlow<MainActivityState>.updateNotVisibleItem(
    elapsedRealTimeWhenBecameVisible: ElapsedRealTimeWhenBecameVisible,
    itemId: ItemId,
    now: Long,
) {
    update { state ->
        state.copy(
            items = state.items.map { item ->
                when (item.id) {
                    itemId -> item.updateTimes(elapsedRealTimeWhenBecameVisible, now)
                    else -> item
                }
            }
        )
    }
}

private fun MainActivityItemUi.updateTimes(
    elapsedRealTimeWhenBecameVisible: ElapsedRealTimeWhenBecameVisible,
    now: Long,
): MainActivityItemUi {
    val timeSinceBecameVisible = now - elapsedRealTimeWhenBecameVisible.value
    val clampedDelta = timeSinceBecameVisible.coerceAtLeast(0L)
    val total = previouslyAccumulatedVisibleTimeInMilliSeconds + clampedDelta
    return when {
        total == visibleTimeInMilliSeconds
                && total == previouslyAccumulatedVisibleTimeInMilliSeconds -> this

        else -> copy(
            previouslyAccumulatedVisibleTimeInMilliSeconds = total,
            visibleTimeInMilliSeconds = total,
            formattedVisibleTimeInSeconds = total.toVisibleTime(),
        )
    }
}

private fun Long.toVisibleTime(): String =
    String.format(Locale.getDefault(), "%.1f", this / 1000f)