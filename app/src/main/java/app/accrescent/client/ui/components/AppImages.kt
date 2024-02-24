package app.accrescent.client.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.accrescent.client.data.Media
import app.accrescent.client.data.MediaListInfo
import app.accrescent.client.ui.theme.Shapes
import coil.compose.AsyncImage

@Composable
fun AppImages(
    appName: String,
    appId: String,
    onImageClick: (MediaListInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageUrl = listOf(
        "https://raw.githubusercontent.com/deckerst/aves_extra/main/screenshots/readme/en/1.png",
        "https://raw.githubusercontent.com/deckerst/aves_extra/main/screenshots/play/en/2.png",
        "https://raw.githubusercontent.com/deckerst/aves_extra/main/screenshots/play/en/5.png",
        "https://raw.githubusercontent.com/deckerst/aves_extra/main/screenshots/play/en/3.png",
        "https://raw.githubusercontent.com/deckerst/aves_extra/main/screenshots/readme/en/4.png",
        "https://raw.githubusercontent.com/deckerst/aves_extra/main/screenshots/play/en/6.png"
    )

    val mediaList = imageUrl.map { Media("${appId}_${appName}_$it", it) }

    LazyRow(
        modifier = modifier
            .height(276.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(mediaList, key = { it.id }) { media ->
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .size(width = 125.dp, height = 276.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                shape = Shapes.large
            ) {
                AsyncImage(
                    modifier = Modifier
                        .clickable {
                            onImageClick(MediaListInfo(mediaList, media.id))
                        }
                        .fillMaxSize(),
                    model = media.url,
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }
        }

    }
}