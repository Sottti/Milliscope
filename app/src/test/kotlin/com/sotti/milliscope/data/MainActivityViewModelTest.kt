package com.sotti.milliscope.data

import com.sotti.milliscope.model.ItemId
import com.sotti.milliscope.model.MainActivityAction.BecameNotVisible
import com.sotti.milliscope.model.MainActivityAction.BecameVisible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class MainActivityViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `accumulates visible time on hide`() {
        val vm = MainActivityViewModel()
        val firstId: ItemId = vm.state.value.items.first().id

        vm.onAction(BecameVisible(firstId))
        Thread.sleep(60)
        vm.onAction(BecameNotVisible(firstId))

        val item = vm.state.value.items.first { it.id == firstId }
        assertTrue(item.visibleTimeInMilliSeconds >= 50L)
    }

    @Test
    fun `repeated BecameVisible does not reset session`() {
        val vm = MainActivityViewModel()
        val firstId: ItemId = vm.state.value.items.first().id

        vm.onAction(BecameVisible(firstId))
        vm.onAction(BecameVisible(firstId))
        Thread.sleep(40)
        vm.onAction(BecameNotVisible(firstId))

        val item = vm.state.value.items.first { it.id == firstId }
        assertTrue(item.visibleTimeInMilliSeconds >= 30L)
    }
}