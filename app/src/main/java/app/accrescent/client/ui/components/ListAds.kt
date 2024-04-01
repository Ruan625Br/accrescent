package app.accrescent.client.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import app.accrescent.client.R
import app.accrescent.client.data.db.App
import app.accrescent.client.data.models.Ads
import app.accrescent.client.data.models.AdsAction
import app.accrescent.client.data.models.AdsType
import app.accrescent.client.ui.theme.InterFontFamily
import app.accrescent.client.ui.theme.Shapes
import app.accrescent.client.util.getDominantColor
import app.accrescent.client.util.getVibrantSwatch
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.FlowPreview
import okhttp3.internal.immutableListOf

@Composable
fun ListAds(
    list: List<Ads>, onNavigateToAppDetails: (App) -> Unit, modifier: Modifier = Modifier
) {

    LazyRow(
        modifier = modifier.height(346.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(list) { ads ->
            AdsItem(ads = ads, onNavigateToAppDetails = onNavigateToAppDetails)
        }
    }
}

@Composable
private fun AdsItem(
    ads: Ads, onNavigateToAppDetails: (App) -> Unit
) {
    val context = LocalContext.current
    val width = LocalConfiguration.current.screenWidthDp - 50
    val imageHeight = if (ads.app == null) 230.dp else 130.dp
    val backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    var dominantColor by remember {
        mutableStateOf(backgroundColor)
    }
    var bitmap: Bitmap? by remember {
        mutableStateOf(null)
    }
    val request =
        ImageRequest.Builder(context).data(ads.imageUrl).bitmapConfig(Bitmap.Config.ARGB_8888)
            .build()
    val painter = rememberAsyncImagePainter(request)
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(key1 = request) {
        request.context.imageLoader.execute(request).drawable?.let {
            bitmap = it.toBitmap()
        }
    }
    LaunchedEffect(key1 = bitmap) {
        bitmap?.let {
            dominantColor = it.getDominantColor(backgroundColor)
        }
    }

    ConstraintLayout(modifier = Modifier
        .clickable {
            when (ads.clickAction) {
                is AdsAction.OpenLink -> {
                    uriHandler.openUri(ads.clickAction.link)
                }

                is AdsAction.ShowAppDetails -> {
                    ads.app?.let {
                        onNavigateToAppDetails(it)
                    }
                }

                else -> Unit
            }
            ads.app?.let {
                onNavigateToAppDetails(it)
            }
        }
        .background(color = dominantColor, shape = Shapes.medium)
        .size(width = width.dp, height = 310.dp)) {

        val (adsImage, adsTitle, adsSubtitle,
            adsApp,
            btnInstallApp) = createRefs()
        val startGuideline = createGuidelineFromStart(10.dp)

        Box(modifier = Modifier
            .height(imageHeight)
            .constrainAs(adsImage) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {

            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painter,
                contentScale = ContentScale.Crop,
                contentDescription = null
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color.Black, dominantColor), startY = 0f, endY = 500f
                        ),
                    ),
            )
        }

        AdsInfo(
            adsType = ads.type, bitmap = bitmap
        )
        Text(
            modifier = Modifier.constrainAs(adsTitle) {
                    top.linkTo(adsImage.bottom)
                    start.linkTo(startGuideline)
                },
            text = ads.title,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
        )

        Text(modifier = Modifier.constrainAs(adsSubtitle) {
                top.linkTo(adsTitle.bottom)
                start.linkTo(startGuideline)
            }, text = ads.subtitle, fontFamily = InterFontFamily, fontSize = 12.sp)

        ads.app?.let { app ->
            AppCard(modifier = Modifier.constrainAs(adsApp) {
                    top.linkTo(adsSubtitle.bottom)
                    start.linkTo(startGuideline)
                }, app = app, isCardTransparent = true)

            OutlinedButton(
                modifier = Modifier
                    .constrainAs(btnInstallApp){
                        bottom.linkTo(parent.bottom, margin = 30.dp)
                        end.linkTo(parent.end, margin = 10.dp)
                    },
                onClick = {
                onNavigateToAppDetails(app)
            }) {
                Text(text = stringResource(id = R.string.install))
            }
        }
    }
}

@Composable
private fun AdsInfo(
    adsType: AdsType, bitmap: Bitmap?, modifier: Modifier = Modifier
) {

    var backgroundColor by remember {
        mutableStateOf(Color(0xFFB6AAAA))
    }
    var titleColor by remember {
        mutableStateOf(Color.Black)
    }

    val title = when (adsType) {
        AdsType.SPONSORED -> "Sponsored"
        AdsType.OFFICIAL -> "Official Accrescent"
    }

    LaunchedEffect(key1 = bitmap) {
        val vibrantSwatch =  bitmap?.getVibrantSwatch()

        if (bitmap != null && vibrantSwatch != null){
            backgroundColor = Color(vibrantSwatch.rgb)
            titleColor = Color(vibrantSwatch.titleTextColor)
        }

    }

    Column(
        modifier = modifier
            .height(30.dp)
            .background(
                color = backgroundColor,
                shape = Shapes.medium.copy(bottomStart = CornerSize(0.dp), topEnd = CornerSize(0.dp))
            )
    ) {
        Text(
            modifier = Modifier
                .padding(4.dp),
            text = title,
            fontFamily = InterFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = titleColor)
    }

}

