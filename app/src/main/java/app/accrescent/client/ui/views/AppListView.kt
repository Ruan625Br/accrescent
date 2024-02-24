package app.accrescent.client.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.accrescent.client.data.db.App
import app.accrescent.client.ui.components.CategoriesWithAppsList
import app.accrescent.client.ui.components.RecentlyAddedAppsList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun AppListView(
    apps: List<App>,
    onClickApp: (App) -> Unit,
    modifier: Modifier = Modifier
) {

    val categories by remember { mutableStateOf(listOf("Productivity", "Tools", "Educational apps")) }
    val randomCategories by remember { mutableStateOf(List(apps.size) { categories.random() }) }
    val randomAppsCategories by remember { mutableStateOf(apps.mapIndexed { index, app -> AppExampleCategory(app.id, app.name, app.minVersionCode, app.iconHash, randomCategories[index]) }) }
    val appsCategoryMap: Map<String, List<AppExampleCategory>> = randomAppsCategories
        .groupBy { it.category }


    Column(
        modifier = modifier
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(25.dp)
    ) {
        RecentlyAddedAppsList(
            apps = apps.take(9).toImmutableList(),
            onClickApp = onClickApp)

        CategoriesWithAppsList(appsCategory = appsCategoryMap, onClickApp = {
            onClickApp(it.toApp())
        })
    }
}

data class AppExampleCategory(
    val id: String,
    val name: String,
    val minVersionCode: Int,
    val iconHash: String,
    val category: String
)

fun AppExampleCategory.toApp(): App = App(
    id, name, minVersionCode, iconHash
)