package com.sotti.milliscope.data

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sotti.milliscope.model.ElapsedRealTimeWhenBecameVisible
import com.sotti.milliscope.model.ItemId
import com.sotti.milliscope.model.MainActivityAction
import com.sotti.milliscope.model.MainActivityAction.BecameNotVisible
import com.sotti.milliscope.model.MainActivityAction.BecameVisible
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal class MainActivityViewModel : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    internal val state = _state.asStateFlow()

    private val visibleItems = mutableMapOf<ItemId, ElapsedRealTimeWhenBecameVisible>()

    init {
        viewModelScope.launch {
            while (isActive) {
                delay(100)
                _state.updateVisibleItems(visibleItems)
            }
        }
    }

    internal fun onAction(action: MainActivityAction) {
        when (action) {
            is BecameVisible -> visibleItems.asVisible(action.itemId)
            is BecameNotVisible -> visibleItems.asNotVisible(action.itemId)
        }
    }

    private fun MutableMap<ItemId, ElapsedRealTimeWhenBecameVisible>.asVisible(itemId: ItemId) {
        getOrPut(itemId) { ElapsedRealTimeWhenBecameVisible(SystemClock.elapsedRealtime()) }
    }

    private fun MutableMap<ItemId, ElapsedRealTimeWhenBecameVisible>.asNotVisible(itemId: ItemId) {
        remove(itemId)?.let { start ->
            _state.updateNotVisibleItems(elapsedRealTimeWhenBecameVisible = start, itemId = itemId)
        }
    }
}
