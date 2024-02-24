package app.accrescent.client.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import app.accrescent.client.R
import app.accrescent.client.data.db.App
import app.accrescent.client.ui.theme.InterFontFamily
import app.accrescent.client.ui.theme.Shapes
import kotlinx.collections.immutable.ImmutableList

@Composable
fun RecentlyAddedAppsList(
    apps: ImmutableList<App>, onClickApp: (App) -> Unit, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(300.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(id = R.string.recently_added),
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
        )

        LazyHorizontalGrid(
            rows = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(apps, key = { it.id }) { app ->
                AppItem(modifier = Modifier.clickable {
                    onClickApp(app)
                }, app = app)
            }
        }
    }
}

@Composable
private fun AppItem(app: App, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .width(280.dp),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        )
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(4.dp)
        ) {
            val (iconRef, appNameRef, appCategoryRef, appStatusMessageRef) = createRefs()

            AppIcon(modifier = Modifier
                .size(70.dp)
                .constrainAs(iconRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }, appId = app.id
            )

            Text(
                modifier = Modifier.constrainAs(appNameRef) {
                    top.linkTo(iconRef.top, margin = 5.dp)
                    start.linkTo(iconRef.end, margin = 5.dp)
                },
                text = app.name,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp
            )

            Text(
                modifier = Modifier.constrainAs(appCategoryRef) {
                    top.linkTo(appNameRef.bottom)
                    start.linkTo(appNameRef.start)
                },
                text = "Category here",
                fontFamily = InterFontFamily,
                fontSize = 14.sp,
                color = Color(0xFFA8A9AD)
            )
            Text(
                modifier = Modifier.constrainAs(appStatusMessageRef) {
                    top.linkTo(appCategoryRef.bottom)
                    start.linkTo(appCategoryRef.start)
                },
                text = "35 MB",
                fontFamily = InterFontFamily,
                fontSize = 14.sp,
                color = Color(0xFFA8A9AD)
            )
        }
    }
}
