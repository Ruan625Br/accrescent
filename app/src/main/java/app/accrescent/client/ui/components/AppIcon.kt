package app.accrescent.client.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import app.accrescent.client.data.REPOSITORY_URL
import app.accrescent.client.ui.theme.Shapes
import coil.compose.SubcomposeAsyncImage

@Composable
fun AppIcon(appId: String, modifier: Modifier = Modifier) {
    SubcomposeAsyncImage(
        model = "$REPOSITORY_URL/apps/$appId/icon.png",
        contentDescription = null,
        modifier= modifier.clip(Shapes.large),
        loading = { CircularProgressIndicator() },
    )
}
