package app.accrescent.client.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import app.accrescent.client.data.db.App
import app.accrescent.client.ui.components.AppIcon
import app.accrescent.client.ui.theme.InterFontFamily
import app.accrescent.client.ui.theme.Shapes

@Composable
fun SearchScreen(
    searchQuery: String,
    onNavigateToAppDetails: (App) -> Unit,
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val apps by searchViewModel.searchedAppsList.collectAsState()

    LaunchedEffect(key1 = searchQuery, block = {
        searchViewModel.onSearchTextChange(searchQuery)
    })

    LazyColumn(modifier = modifier) {
        items(apps, key = { it.id }) { app ->
            AppItem(
                modifier = Modifier
                    .clickable { onNavigateToAppDetails(app) },
                app = app
            )
        }
    }
}


@Composable
private fun AppItem(
    app: App,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(8.dp)
        ) {
            val (iconRef, appNameRef, appCategoryRef,
                appStatusMessageRef) = createRefs()

            AppIcon(
                modifier = Modifier
                    .size(60.dp)
                    .constrainAs(iconRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }, appId = app.id
            )

            Text(
                modifier = Modifier.constrainAs(appNameRef) {
                    top.linkTo(iconRef.top)
                    start.linkTo(iconRef.end, margin = 5.dp)
                },
                text = app.name,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )

            Text(
                modifier = Modifier.constrainAs(appCategoryRef) {
                    top.linkTo(appNameRef.bottom)
                    start.linkTo(appNameRef.start)
                },
                text = "Author",
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