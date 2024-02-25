package app.accrescent.client.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import app.accrescent.client.ui.theme.Shapes

@Composable
fun CardComponent(
    modifier: Modifier = Modifier,
    shape: Shape = Shapes.large,
    isTransparent: Boolean = false,
    content: @Composable (ColumnScope.() -> Unit)
) {
    val containerColor = if (isTransparent) {
        Color.Transparent
    } else {
        MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
    }
    Card(
        modifier = modifier.fillMaxWidth(), shape = shape, colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        content()
    }
}
