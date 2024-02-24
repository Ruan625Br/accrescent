package app.accrescent.client.ui.screens.applist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.accrescent.client.R
import app.accrescent.client.data.InstallStatus
import app.accrescent.client.ui.components.CenteredText
import app.accrescent.client.ui.screens.Screen
import app.accrescent.client.ui.views.AppListView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppList(
    navController: NavController,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    viewModel: AppListViewModel = hiltViewModel(),
    filter: (installStatus: InstallStatus) -> Boolean = { true },
    noFilterResultsText: String = "",
) {
    val apps by viewModel.apps.collectAsState(emptyList())
    val scope = rememberCoroutineScope()
    val installStatuses = viewModel.installStatuses
    val filteredApps = apps.filter { filter(installStatuses[it.id] ?: InstallStatus.LOADING) }
        .sortedBy { it.name.lowercase() }

    val refreshScope = rememberCoroutineScope()
    val state = rememberPullRefreshState(viewModel.isRefreshing, onRefresh = {
        refreshScope.launch {
            viewModel.refreshRepoData()
            viewModel.refreshInstallStatuses()
        }
    })
    val listState = rememberLazyListState()
    DisposableEffect(apps) {
        val currentRoute = navController.currentDestination?.route
        scope.launch {
            // restore the previous scroll state
            val (index, offset) = viewModel.firstVisibleItems.getOrDefault(currentRoute, Pair(0, 0))
            listState.scrollToItem(index, offset)
        }
        onDispose {
            // save the scroll state, while it's not equal to 0 which can happen due to animations
            if (listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0) return@onDispose
            viewModel.firstVisibleItems[currentRoute] = Pair(
                listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset
            )
        }
    }

    Box(modifier.pullRefresh(state)) {
        val verticalArrangement =
            if (apps.isEmpty() || filteredApps.isEmpty()) Arrangement.Center else Arrangement.Top

        LazyColumn(
            Modifier.fillMaxSize(), verticalArrangement = verticalArrangement, state = listState
        ) {

            when {
                apps.isEmpty() -> {
                    item { CenteredText(stringResource(R.string.swipe_refresh)) }
                }

                filteredApps.isEmpty() -> {
                    item { CenteredText(noFilterResultsText) }
                }

                else -> {
                    item { Spacer(Modifier.height(16.dp)) }

                    item {
                        AppListView(apps = filteredApps, onClickApp = { app ->
                            navController.navigate("${Screen.AppDetails.route}/${app.id}")
                        })
                    }
                }
            }
        }

        if (viewModel.error != null) {
            LaunchedEffect(snackbarHostState) {
                snackbarHostState.showSnackbar(message = viewModel.error!!)
                viewModel.error = null
            }
        }

        PullRefreshIndicator(
            refreshing = viewModel.isRefreshing,
            state = state,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}
