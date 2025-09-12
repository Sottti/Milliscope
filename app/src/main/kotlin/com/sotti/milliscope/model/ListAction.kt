package com.sotti.milliscope.model

internal sealed interface ListAction {
    data class ItemNotVisible(val itemId: ItemId) : ListAction
    data class ItemVisible(val itemId: ItemId) : ListAction
    data object ListNotVisible : ListAction
    data object ListVisible : ListAction
}