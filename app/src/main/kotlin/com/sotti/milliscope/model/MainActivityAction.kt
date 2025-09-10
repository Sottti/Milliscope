package com.sotti.milliscope.model

internal sealed interface MainActivityAction {
    data class BecameNotVisible(val itemId: ItemId) : MainActivityAction
    data class BecameVisible(val itemId: ItemId) : MainActivityAction
}