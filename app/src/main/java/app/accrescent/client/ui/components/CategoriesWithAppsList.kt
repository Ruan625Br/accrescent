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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.accrescent.client.ui.theme.InterFontFamily
import app.accrescent.client.ui.theme.Shapes
import app.accrescent.client.ui.views.AppExampleCategory

@Composable
fun CategoriesWithAppsList(
    appsCategory: Map<String, List<AppExampleCategory>>,
    onClickApp: (AppExampleCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(600.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        for ((category, apps) in appsCategory) {
            Text(
                text = category,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(apps, key = { it.id }) { app ->
                    AppItem(modifier = Modifier
                        .clickable { onClickApp(app) },
                        app = app)
                }
            }
        }
    }
}

@Composable
private fun AppItem(
    app: AppExampleCategory,
    modifier: Modifier = Modifier
){
    Card(
        modifier = modifier
            .size(width = 110.dp, height = 150.dp),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        )
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {

        AppIcon(modifier = Modifier
            .clip(Shapes.large)
            .size(85.dp), appId = app.id)

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