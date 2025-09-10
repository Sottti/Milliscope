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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sotti.milliscope.data.MainActivityViewModel
import com.sotti.milliscope.model.ItemId
import com.sotti.milliscope.model.MainActivityAction
import com.sotti.milliscope.model.MainActivityAction.BecameNotVisible
import com.sotti.milliscope.model.MainActivityAction.BecameVisible
import com.sotti.milliscope.model.MainActivityItemUi
import com.sotti.milliscope.model.MainActivityState
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
internal fun MainActivityUi(
    viewModel: MainActivityViewModel,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    MainActivityUi(
        state = state.value,
        onAction = viewModel.onAction,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainActivityUi(
    state: MainActivityState,
    onAction: (MainActivityAction) -> Unit,
) {
    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = { TopBar(state, scrollBehavior) },
    ) { padding ->
        List(
            listState = listState,
            onAction = onAction,
            padding = padding,
            scrollBehavior = scrollBehavior,
            state = state,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar(
    state: MainActivityState,
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
    listState: LazyListState,
    onAction: (MainActivityAction) -> Unit,
    padding: PaddingValues,
    scrollBehavior: TopAppBarScrollBehavior,
    state: MainActivityState,
) {
    LazyColumn(
        state = listState,
        contentPadding = padding,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        items(
            count = state.items.size,
            key = { index -> state.items[index].id.value },
        ) { index -> Item(item = state.items[index]) }
    }

    NotifyVisibilityChanges(state = state, listState = listState, onAction = onAction)
}

@Composable
private fun NotifyVisibilityChanges(
    listState: LazyListState,
    onAction: (MainActivityAction) -> Unit,
    state: MainActivityState,
) {
    val idsByIndexState = rememberUpdatedState(newValue = state.items.map { it.id })

    LaunchedEffect(listState) {
        var prev: Set<ItemId> = emptySet()
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.map { it.index }.sorted() }
            .distinctUntilChanged()
            .collect { visibleIndices ->
                val idsByIndex = idsByIndexState.value
                val visibleIds = visibleIndices.map { idsByIndex[it] }.toSet()
                val becameVisible = visibleIds - prev
                val becameHidden = prev - visibleIds
                becameVisible.forEach { onAction(BecameVisible(it)) }
                becameHidden.forEach { onAction(BecameNotVisible(it)) }
                prev = visibleIds
            }
    }
}

@Composable
private fun Item(
    item: MainActivityItemUi,
    //onAction: (MainActivityAction) -> Unit,
) {
    Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        ListItem(
//        modifier = Modifier.onVisibilityChanged(
//            minDurationMs = 0,
//            minFractionVisible = 1f
//        ) { isVisible ->
//            when {
//                isVisible -> onAction(BecameVisible(item.id))
//                else -> onAction(BecameNotVisible(item.id))
//            }
//        },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            headlineContent = { Text(text = item.label) },
            trailingContent = { Text(text = item.formattedVisibleTimeInSeconds) },
        )
    }
}