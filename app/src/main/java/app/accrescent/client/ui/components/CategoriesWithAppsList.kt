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
import app.accrescent.client.ui.theme.InterFontFamily
import app.accrescent.client.ui.theme.Shapes
import app.accrescent.client.ui.views.AppExampleCategory

@Composable
fun CategoriesWithAppsList(
    appsCategory: Map<String, List<AppExampleCategory>>,
    isCardTransparent: Boolean,
    onClickApp: (AppExampleCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.height(600.dp), verticalArrangement = Arrangement.spacedBy(15.dp)
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
                    AppItem(
                        modifier = Modifier.clickable { onClickApp(app) },
                        app = app,
                        isCardTransparent = isCardTransparent
                    )
                }
            }
        }
    }
}

@Composable
private fun AppItem(
    app: AppExampleCategory, isCardTransparent: Boolean, modifier: Modifier = Modifier
) {
    CardComponent(
        isTransparent = isCardTransparent,
        modifier = modifier.size(width = 110.dp, height = 150.dp),
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
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = "Author here",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFFA8A9AD)
            )
        }
    }
}