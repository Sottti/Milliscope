package com.sottti.milliscope.model

import androidx.annotation.StringRes

internal data class MainActivityState(
    @StringRes val titleResId: Int,
    val items: List<MainActivityItemUi>,
)
