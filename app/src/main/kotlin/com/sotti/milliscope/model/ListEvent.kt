package com.sotti.milliscope.model

import androidx.compose.runtime.Immutable

@Immutable
internal sealed interface ListEvent {
    @Immutable
    object UpdateVisibleItems : ListEvent
}
