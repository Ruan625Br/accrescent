package app.accrescent.client.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import app.accrescent.client.data.db.App
import app.accrescent.client.ui.theme.InterFontFamily

@Composable
fun AppCard(
    app: App,
    isCardTransparent: Boolean,
    modifier: Modifier = Modifier
) {
    CardComponent(
        isTransparent = isCardTransparent,
        modifier = modifier
    ) {
        ConstraintLayout(
            modifier = Modifier.padding(4.dp)
        ) {
            val (iconRef, appNameRef, appAuthorRef, appStatusMessageRef) = createRefs()

            AppIcon(
                modifier = Modifier
                    .size(60.dp)
                    .constrainAs(iconRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start, margin = 10.dp)
                    }, appId = app.id
            )

            Text(
                modifier = Modifier.constrainAs(appNameRef) {
                    top.linkTo(iconRef.top)
                    start.linkTo(iconRef.end, margin = 10.dp)
                },
                text = app.name,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp
            )

            Text(
                modifier = Modifier.constrainAs(appAuthorRef) {
                    top.linkTo(appNameRef.bottom)
                    start.linkTo(appNameRef.start)
                },
                text = "Author here",
                fontFamily = InterFontFamily,
                fontSize = 14.sp,
                color = Color(0xFFA8A9AD)
            )
            Text(
                modifier = Modifier.constrainAs(appStatusMessageRef) {
                    top.linkTo(appAuthorRef.bottom)
                    start.linkTo(appAuthorRef.start)
                },
                text = "35 MB",
                fontFamily = InterFontFamily,
                fontSize = 14.sp,
                color = Color(0xFFA8A9AD)
            )
        }
    }
}