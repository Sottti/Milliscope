package com.sotti.milliscope.data

internal class FakeClock(var nowMs: Long = 0L) : ElapsedRealtimeClock {
    override fun now(): Long = nowMs
}
