package com.sotti.milliscope.model

internal data class MainActivityItemUi(
    val formattedVisibleTimeInSeconds: String,
    val id: ItemId,
    val label: String,
    val previouslyAccumulatedVisibleTimeInMilliSeconds: Long,
    val visibleTimeInMilliSeconds: Long,
)