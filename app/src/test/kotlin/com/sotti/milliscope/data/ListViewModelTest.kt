package com.sotti.milliscope.data

import com.sotti.milliscope.model.ItemId
import com.sotti.milliscope.model.ListAction.ItemNotVisible
import com.sotti.milliscope.model.ListAction.ItemVisible
import com.sotti.milliscope.model.ListAction.ListNotVisible
import com.sotti.milliscope.model.ListAction.ListVisible
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ListViewModelTest {


    @Test
    fun `start - visible - stop`() = runTest {
        val clock = FakeClock(0L)
        val viewModel = ListViewModel(clock = clock)
        val firstItemId: ItemId = viewModel.state.value.items.first().id

        viewModel.onAction(ListVisible)
        viewModel.onAction(ItemVisible(firstItemId))
        clock.nowMs += 100
        viewModel.onAction(ListNotVisible)
        clock.nowMs += 100

        val firstItem = viewModel.state.value.items.first { it.id == firstItemId }
        assertTrue(firstItem.visibleTimeInMilliSeconds == 100L)
    }


    @Test
    fun `multiple ListVisible does not start duplicate ticker`() = runTest {
        val clock = FakeClock(0L)
        val viewModel = ListViewModel(clock = clock)
        val id = viewModel.state.value.items.first().id

        viewModel.onAction(ListVisible)
        viewModel.onAction(ListVisible)
        viewModel.onAction(ItemVisible(id))

        clock.nowMs += 100
        viewModel.onAction(ItemNotVisible(id))

        val item = viewModel.state.value.items.first { it.id == id }
        assertTrue(item.visibleTimeInMilliSeconds == 100L)
    }

    @Test
    fun `ItemNotVisible on non-visible id is no-op`() = runTest {
        val clock = FakeClock(0L)
        val viewModel = ListViewModel(clock = clock)
        val id = viewModel.state.value.items.first().id

        viewModel.onAction(ListVisible)
        viewModel.onAction(ItemNotVisible(id))

        val item = viewModel.state.value.items.first { it.id == id }
        assertTrue(item.visibleTimeInMilliSeconds == 0L)
    }

    @Test
    fun `two items accrue independently`() = runTest {  // (name unchanged)
        val clock = FakeClock(0L)
        val viewModel = ListViewModel(clock = clock)
        val ids = viewModel.state.value.items.take(2).map { it.id }

        viewModel.onAction(ListVisible)
        viewModel.onAction(ItemVisible(ids[0]))
        clock.nowMs += 100
        viewModel.onAction(ItemVisible(ids[1]))
        clock.nowMs += 100
        viewModel.onAction(ItemNotVisible(ids[0]))
        clock.nowMs += 100
        viewModel.onAction(ItemNotVisible(ids[1]))

        val a = viewModel.state.value.items.first { it.id == ids[0] }
        val b = viewModel.state.value.items.first { it.id == ids[1] }
        assertTrue(a.visibleTimeInMilliSeconds == 200L)
        assertTrue(b.visibleTimeInMilliSeconds == 200L)
    }

    @Test
    fun `visible segments accumulate across sessions`() = runTest {  // (name unchanged)
        val clock = FakeClock(0L)
        val viewModel = ListViewModel(clock = clock)
        val id = viewModel.state.value.items.first().id

        viewModel.onAction(ListVisible)
        viewModel.onAction(ItemVisible(id))
        clock.nowMs += 120
        viewModel.onAction(ItemNotVisible(id))
        clock.nowMs += 80
        viewModel.onAction(ItemVisible(id))
        clock.nowMs += 130
        viewModel.onAction(ItemNotVisible(id))

        val item = viewModel.state.value.items.first { it.id == id }
        assertTrue(item.visibleTimeInMilliSeconds == 250L)
    }

    @Test
    fun `no accrual without ItemVisible`() = runTest {
        val clock = FakeClock(0L)
        val viewModel = ListViewModel(clock = clock)
        val id = viewModel.state.value.items.first().id

        viewModel.onAction(ListVisible)
        clock.nowMs += 500

        val item = viewModel.state.value.items.first { it.id == id }
        assertTrue(item.visibleTimeInMilliSeconds == 0L)
    }

    @Test
    fun `rapid list became visible and list not visible toggles do not overcount`() = runTest {
        val clock = FakeClock(0L)
        val viewModel = ListViewModel(clock = clock)
        val id = viewModel.state.value.items.first().id

        repeat(3) {
            viewModel.onAction(ListVisible)
            viewModel.onAction(ItemVisible(id))
            clock.nowMs += 100
            viewModel.onAction(ItemNotVisible(id))
            clock.nowMs += 50
        }

        val item = viewModel.state.value.items.first { it.id == id }
        assertTrue(item.visibleTimeInMilliSeconds == 300L)
    }
}