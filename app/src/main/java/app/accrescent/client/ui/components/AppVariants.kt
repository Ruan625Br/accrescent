package app.accrescent.client.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.accrescent.client.data.db.App
import app.accrescent.client.ui.theme.InterFontFamily
import app.accrescent.client.ui.theme.Shapes
import kotlinx.collections.immutable.ImmutableList

@Composable
fun AppVariants(
    apps: ImmutableList<App>, onNavigateToAppDetails: (App) -> Unit, modifier: Modifier = Modifier
) {

    LazyRow(
        modifier = modifier.height(120.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {

        items(apps) { app ->
            AppItemRow(
                modifier = Modifier.clickable {
                    onNavigateToAppDetails(app)
                }, app = app
            )
        }
    }
}

@Composable
private fun AppItemRow(
    app: App, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.size(width = 110.dp, height = 150.dp),
        shape = Shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AppIcon(
                modifier = Modifier
                    .clip(Shapes.large)
                    .size(60.dp), appId = app.id
            )

            Text(
                text = app.name,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}