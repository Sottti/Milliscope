package com.sotti.milliscope.data

import com.sotti.milliscope.R
import com.sotti.milliscope.model.ItemId
import com.sotti.milliscope.model.MainActivityItemUi
import com.sotti.milliscope.model.MainActivityState

private val titleResId = R.string.app_name
private val initialList =
    List(138) { index ->
        MainActivityItemUi(
            formattedVisibleTimeInSeconds = "0",
            id = ItemId(index + 1),
            label = "Item ${index + 1}",
            visibleTimeInMilliSeconds = 0L,
            previouslyAccumulatedVisibleTimeInMilliSeconds = 0L,
        )
    }

internal val initialState = MainActivityState(
    titleResId = titleResId,
    items = initialList,
)