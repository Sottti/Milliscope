package com.sotti.milliscope.data

import com.sotti.milliscope.model.ItemId
import com.sotti.milliscope.model.ListAction.ItemNotVisible
import com.sotti.milliscope.model.ListAction.ItemVisible
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ListViewModelTest {

    @Test
    fun `basic start - stop - start - stop scenario`() {
        val clock = FakeClock(0L)
        val viewModel = ListViewModel(clock = clock)
        val firstItemId: ItemId = viewModel.state.value.items.first().id
        val secondItemId: ItemId = viewModel.state.value.items[1].id
        val thirdItemId: ItemId = viewModel.state.value.items[2].id

        viewModel.onAction(ItemVisible(firstItemId))
        viewModel.onAction(ItemVisible(secondItemId))
        clock.nowMs += 100
        viewModel.onAction(ItemNotVisible(firstItemId))
        clock.nowMs += 100
        viewModel.onAction(ItemVisible(firstItemId))
        clock.nowMs += 100
        viewModel.onAction(ItemNotVisible(firstItemId))
        viewModel.onAction(ItemNotVisible(secondItemId))

        val firstItem = viewModel.state.value.items.first { it.id == firstItemId }
        assertTrue(firstItem.visibleTimeInMilliSeconds == 200L)

        val secondItem = viewModel.state.value.items.first { it.id == secondItemId }
        assertTrue(secondItem.visibleTimeInMilliSeconds == 300L)

        val thirdItem = viewModel.state.value.items.first { it.id == thirdItemId }
        assertTrue(thirdItem.visibleTimeInMilliSeconds == 0L)
    }
}