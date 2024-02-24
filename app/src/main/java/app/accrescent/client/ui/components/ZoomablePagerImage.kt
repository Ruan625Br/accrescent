package app.accrescent.client.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import app.accrescent.client.data.Media
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ZoomablePagerImage(
    media: Media,
    onItemClick: () -> Unit,
    onZoomScaleChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    maxScale: Float = 25f,
    maxImageSize: Int = 4096
) {

    val zoomState = rememberZoomState(
        maxScale = maxScale
    )
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(media.url)
            .size(maxImageSize)
            .build(),
        contentScale = ContentScale.Fit,
        filterQuality = FilterQuality.None,
        onSuccess = {
            zoomState.setContentSize(it.painter.intrinsicSize)
        })

    LaunchedEffect(zoomState.scale){
        onZoomScaleChange(zoomState.scale)
    }

    Image(
        modifier = modifier
            .fillMaxSize()
            .combinedClickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onDoubleClick = {},
                onClick = onItemClick
            )
            .zoomable(
                zoomState = zoomState
            ),
        painter = painter,
        contentDescription = null)
}