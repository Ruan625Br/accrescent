package app.accrescent.client.ui.screens.mediaview

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.accrescent.client.data.Media
import app.accrescent.client.data.MediaListInfo
import app.accrescent.client.ui.components.ZoomablePagerImage


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaViewScreen(
    mediaListInfo: MediaListInfo,
) {
    val mediaList = mediaListInfo.mediaList
    val currentMediaId = mediaListInfo.currentMediaId

    var scrollEnabled by rememberSaveable {
        mutableStateOf(true)
    }
    val currentMedia = rememberSaveable { mutableStateOf<Media?>(null) }
    var runtimeMediaId by rememberSaveable(currentMediaId) {
        mutableStateOf(currentMediaId)
    }
    val initialPage = rememberSaveable(runtimeMediaId) {
        mediaList.indexOfFirst { it.id == runtimeMediaId }
    }

    val pagerState = rememberPagerState(
        initialPage = initialPage, initialPageOffsetFraction = 0f, pageCount = mediaList::size
    )
    val lastIndex = remember {
        mutableIntStateOf(-1)
    }

    val updateContent: (Int) -> Unit = { page ->
        if (mediaList.isNotEmpty()) {
            val index = if (page == -1) 0 else page
            if (lastIndex.intValue != -1) {
                runtimeMediaId = mediaList[lastIndex.intValue.coerceAtMost(mediaList.size - 1)].id
            }
            currentMedia.value = mediaList[index]
        }
    }

    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = scrollEnabled,
            flingBehavior = PagerDefaults.flingBehavior(
                state = pagerState, lowVelocityAnimationSpec = tween(
                    easing = LinearEasing, durationMillis = 150
                )
            ),
            key = { index ->
                mediaList[index.coerceAtMost(mediaList.size - 1)]
            },
            pageSpacing = 16.dp
        ) {index ->
            ZoomablePagerImage(
                media = mediaList[index],
                onItemClick = { /*TODO*/ },
                onZoomScaleChange = {
                    scrollEnabled = it == 1f
                })
        }
    }

    LaunchedEffect(mediaList){
        updateContent(pagerState.currentPage)
    }

    LaunchedEffect(pagerState){
        snapshotFlow { pagerState.currentPage }.collect { page ->
            updateContent(page)
        }
    }
}