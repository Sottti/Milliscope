package com.sotti.milliscope.data

internal fun interface ElapsedRealtimeClock {
    fun now(): Long
}
