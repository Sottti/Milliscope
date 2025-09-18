package com.sotti.milliscope.data

import com.sotti.milliscope.model.ElapsedRealTimeWhenBecameVisible
import com.sotti.milliscope.model.ItemId
import com.sotti.milliscope.model.ListItemUi
import com.sotti.milliscope.model.ListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

internal fun MutableStateFlow<ListState>.updateVisibleItems(
    now: Long,
    visibleItems: Map<ItemId, ElapsedRealTimeWhenBecameVisible>,
) {
    if (visibleItems.isEmpty()) return
    update { state ->
        var changed = false
        val newItems = state.items.map { item ->
            val updated = item.updateItem(now = now, visibleItems = visibleItems)
            if (updated !== item) changed = true
            updated
        }
        if (changed) state.copy(items = newItems) else state
    }
}

private fun ListItemUi.updateItem(
    now: Long,
    visibleItems: Map<ItemId, ElapsedRealTimeWhenBecameVisible>,
): ListItemUi {
    val start = visibleItems[id]?.value
    val total = start?.let {
        val clampedDelta = (now - it).coerceAtLeast(0L)
        previouslyAccumulatedVisibleTimeInMilliSeconds + clampedDelta
    } ?: previouslyAccumulatedVisibleTimeInMilliSeconds

    return when (total) {
        visibleTimeInMilliSeconds -> this
        else -> copy(
            formattedVisibleTimeInSeconds = total.toVisibleTime(),
            visibleTimeInMilliSeconds = total,
        )
    }
}

internal fun MutableStateFlow<ListState>.updateNotVisibleItem(
    elapsedRealTimeWhenBecameVisible: ElapsedRealTimeWhenBecameVisible,
    itemId: ItemId,
    now: Long,
) {
    update { state ->
        val index: Int = state.items.indexOfFirst { it.id == itemId }
        if (index == -1) return@update state

        val old: ListItemUi = state.items[index]
        val updated: ListItemUi = old.updateTimes(elapsedRealTimeWhenBecameVisible, now)

        if (updated === old) return@update state

        val newItems: MutableList<ListItemUi> = state.items.toMutableList()
        newItems[index] = updated

        state.copy(items = newItems)
    }
}


private fun ListItemUi.updateTimes(
    elapsedRealTimeWhenBecameVisible: ElapsedRealTimeWhenBecameVisible,
    now: Long,
): ListItemUi {
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
    String.format(locale = Locale.getDefault(), format = "%.1f", this / 1000f) + " seconds"
