package com.sotti.milliscope.model

import androidx.annotation.StringRes

internal data class ListState(
    @StringRes val titleResId: Int,
    val items: List<ListItemUi>,
)