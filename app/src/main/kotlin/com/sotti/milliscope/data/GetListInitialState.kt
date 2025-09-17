package com.sotti.milliscope.data

import com.sotti.milliscope.R
import com.sotti.milliscope.model.ItemId
import com.sotti.milliscope.model.ListItemUi
import com.sotti.milliscope.model.ListState

internal class GetListInitialState {

    operator fun invoke(): ListState {
        val titleResId = R.string.app_name
        val items = getItems()
        return ListState(
            titleResId = titleResId,
            items = items,
        )
    }

    private fun getItems(): List<ListItemUi> = List(DEFAULT_SIZE) { index ->
        ListItemUi(
            formattedVisibleTimeInSeconds = "0.0 seconds",
            id = ItemId(index + 1),
            label = "Item ${index + 1}",
            visibleTimeInMilliSeconds = 0L,
            previouslyAccumulatedVisibleTimeInMilliSeconds = 0L,
        )
    }

    private companion object Companion {
        private const val DEFAULT_SIZE = 138
    }
}
