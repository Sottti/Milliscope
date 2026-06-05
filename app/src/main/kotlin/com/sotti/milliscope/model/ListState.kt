package com.sotti.milliscope.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
internal data class ListState(
    @param:StringRes val titleResId: Int,
    val items: List<ListItemUi>,
)
