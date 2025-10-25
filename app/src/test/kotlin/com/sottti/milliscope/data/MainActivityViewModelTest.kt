package com.sottti.milliscope.data

import com.sottti.milliscope.model.ItemId
import com.sottti.milliscope.model.MainActivityAction.BecameNotVisible
import com.sottti.milliscope.model.MainActivityAction.BecameVisible
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class MainActivityViewModelTest {

    @Test
    fun `basic start - stop - start - stop scenario`() {
        val clock = FakeClock(0L)
        val viewModel = MainActivityViewModel(clock)
        val firstItemId: ItemId = viewModel.state.value.items.first().id
        val secondItemId: ItemId = viewModel.state.value.items[1].id
        val thirdItemId: ItemId = viewModel.state.value.items[2].id

        viewModel.onAction(BecameVisible(firstItemId))
        viewModel.onAction(BecameVisible(secondItemId))
        clock.nowMs += 100
        viewModel.onAction(BecameNotVisible(firstItemId))
        clock.nowMs += 100
        viewModel.onAction(BecameVisible(firstItemId))
        clock.nowMs += 100
        viewModel.onAction(BecameNotVisible(firstItemId))
        viewModel.onAction(BecameNotVisible(secondItemId))

        val firstItem = viewModel.state.value.items.first { it.id == firstItemId }
        assertTrue(firstItem.visibleTimeInMilliSeconds == 200L)

        val secondItem = viewModel.state.value.items.first { it.id == secondItemId }
        assertTrue(secondItem.visibleTimeInMilliSeconds == 300L)

        val thirdItem = viewModel.state.value.items.first { it.id == thirdItemId }
        assertTrue(thirdItem.visibleTimeInMilliSeconds == 0L)
    }
}
