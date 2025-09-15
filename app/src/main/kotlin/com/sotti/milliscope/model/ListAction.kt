package com.sotti.milliscope.model

import androidx.compose.runtime.Immutable

@Immutable
internal sealed interface ListAction {
    @Immutable
    data class ItemNotVisible(val itemId: ItemId) : ListAction

    @Immutable
    data class ItemVisible(val itemId: ItemId) : ListAction

    @Immutable
    data object ListNotVisible : ListAction

    @Immutable
    data object ListVisible : ListAction
}