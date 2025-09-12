package com.sotti.milliscope.model

internal data class ListItemUi(
    val formattedVisibleTimeInSeconds: String,
    val id: ItemId,
    val label: String,
    val previouslyAccumulatedVisibleTimeInMilliSeconds: Long,
    val visibleTimeInMilliSeconds: Long,
)