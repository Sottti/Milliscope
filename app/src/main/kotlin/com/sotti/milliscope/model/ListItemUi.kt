package com.sotti.milliscope.model

import androidx.compose.runtime.Immutable

@Immutable
internal data class ListItemUi(
    val formattedVisibleTimeInSeconds: String,
    val id: ItemId,
    val label: String,
    val previouslyAccumulatedVisibleTimeInMilliSeconds: Long,
    val visibleTimeInMilliSeconds: Long,
)
