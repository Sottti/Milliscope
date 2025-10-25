package com.sottti.milliscope.data

internal fun interface ElapsedRealtimeClock {
    fun now(): Long
}
