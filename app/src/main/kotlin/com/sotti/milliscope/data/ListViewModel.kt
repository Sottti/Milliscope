package com.sotti.milliscope.data

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sotti.milliscope.model.ElapsedRealTimeWhenBecameVisible
import com.sotti.milliscope.model.ItemId
import com.sotti.milliscope.model.ListAction
import com.sotti.milliscope.model.ListAction.ItemNotVisible
import com.sotti.milliscope.model.ListAction.ItemVisible
import com.sotti.milliscope.model.ListAction.ListNotVisible
import com.sotti.milliscope.model.ListAction.ListVisible
import com.sotti.milliscope.model.ListEvent
import com.sotti.milliscope.model.ListEvent.UpdateVisibleItems
import com.sotti.milliscope.model.ListState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

internal class ListViewModel(
    getListInitialState: GetListInitialState = GetListInitialState(),
    private val clock: ElapsedRealtimeClock = ElapsedRealtimeClock { SystemClock.elapsedRealtime() },
) : ViewModel() {
    private val _state = MutableStateFlow(getListInitialState())
    internal val state: StateFlow<ListState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<ListEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    internal val onAction: (ListAction) -> Unit = ::processAction

    private var tickerJob: Job? = null

    private val visibleItems = mutableMapOf<ItemId, ElapsedRealTimeWhenBecameVisible>()

    private val refreshRate = 100.milliseconds

    private fun processAction(action: ListAction) {
        when (action) {
            is ItemVisible -> visibleItems.asVisible(action.itemId)
            is ItemNotVisible -> visibleItems.asNotVisible(action.itemId)
            ListVisible -> {
                startTicker()
                _events.tryEmit(UpdateVisibleItems)
            }

            ListNotVisible -> {
                stopTicker()
                visibleItems.keys.toList().forEach { itemId -> visibleItems.asNotVisible(itemId) }
            }
        }
    }

    private fun MutableMap<ItemId, ElapsedRealTimeWhenBecameVisible>.asVisible(itemId: ItemId) {
        putIfAbsent(itemId, ElapsedRealTimeWhenBecameVisible(clock.now()))
    }

    private fun MutableMap<ItemId, ElapsedRealTimeWhenBecameVisible>.asNotVisible(itemId: ItemId) {
        val start = remove(itemId) ?: return
        _state.updateNotVisibleItem(
            elapsedRealTimeWhenBecameVisible = start,
            itemId = itemId,
            now = clock.now()
        )
    }

    private fun startTicker() {
        if (tickerJob?.isActive == true) return
        tickerJob = viewModelScope.launch {
            while (isActive) {
                delay(duration = refreshRate)
                _state.updateVisibleItems(clock.now(), visibleItems)
            }
        }
    }

    private fun stopTicker() {
        tickerJob?.cancel()
        tickerJob = null
    }

    override fun onCleared() {
        stopTicker()
        super.onCleared()
    }
}