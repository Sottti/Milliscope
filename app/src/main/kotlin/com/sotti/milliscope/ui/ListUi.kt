package com.sotti.milliscope.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.sotti.milliscope.data.ListViewModel
import com.sotti.milliscope.model.ListAction
import com.sotti.milliscope.model.ListAction.ItemNotVisible
import com.sotti.milliscope.model.ListAction.ItemVisible
import com.sotti.milliscope.model.ListEvent
import com.sotti.milliscope.model.ListEvent.UpdateVisibleItems
import com.sotti.milliscope.model.ListItemUi
import com.sotti.milliscope.model.ListState
import kotlinx.coroutines.flow.Flow

@Composable
internal fun ListUi(
    viewModel: ListViewModel,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    ListUi(
        events = viewModel.events,
        onAction = viewModel.onAction,
        state = state,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ListUi(
    events: Flow<ListEvent>,
    onAction: (ListAction) -> Unit,
    state: State<ListState>,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = { TopBar(state.value, scrollBehavior) },
    ) { padding ->
        List(
            events = events,
            onAction = onAction,
            padding = padding,
            scrollBehavior = scrollBehavior,
            state = state.value,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar(
    state: ListState,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        title = { Text(text = stringResource(state.titleResId)) },
        scrollBehavior = scrollBehavior,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun List(
    events: Flow<ListEvent>,
    onAction: (ListAction) -> Unit,
    padding: PaddingValues,
    scrollBehavior: TopAppBarScrollBehavior,
    state: ListState,
) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        contentPadding = padding,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        items(
            count = state.items.size,
            key = { index -> state.items[index].id.value },
        ) { index -> Item(item = state.items[index], onAction = onAction) }
    }

    ObserveEvents(events = events, listState = listState, onAction = onAction, state = state)
}

@Composable
private fun ObserveEvents(
    events: Flow<ListEvent>,
    listState: LazyListState,
    onAction: (ListAction) -> Unit,
    state: ListState,
) {
    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle
    val latestState = rememberUpdatedState(state)

    LaunchedEffect(events, lifecycle) {
        lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
            events.collect { event ->
                when (event) {
                    UpdateVisibleItems -> updateVisibleItems(
                        listState = listState,
                        state = latestState.value,
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

private fun updateVisibleItems(
    listState: LazyListState,
    state: ListState,
    onAction: (ListAction) -> Unit,
) {
    listState
        .layoutInfo
        .visibleItemsInfo
        .map { it.index }
        .sorted()
        .forEach { index ->
            val itemId = state.items.getOrNull(index)?.id ?: return@forEach
            onAction(ItemVisible(itemId))
        }
}

@Composable
private fun Item(
    item: ListItemUi,
    onAction: (ListAction) -> Unit,
) {
    Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        ListItem(
            modifier = Modifier.onVisibilityChanged(
                minDurationMs = 0,
                minFractionVisible = 1f
            ) { isVisible ->
                when {
                    isVisible -> onAction(ItemVisible(item.id))
                    else -> onAction(ItemNotVisible(item.id))
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            headlineContent = { Text(text = item.label) },
            trailingContent = { Text(text = item.formattedVisibleTimeInSeconds) },
        )
    }
}
