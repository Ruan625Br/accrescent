package app.accrescent.client.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.ui.graphics.vector.ImageVector


data class LinkTextData(
    val text: String,
    val tag: String,
    val link: String,
    val linkType: CommonsLinkType = CommonsLinkType.GENERIC
)

enum class CommonsLinkType(
    val icon: ImageVector? = null
){
    LICENSE(Icons.Rounded.Gavel),
    SOURCE_CODE(Icons.Rounded.Code),
    ISSUE_TRACKER(Icons.Rounded.BugReport),
    BUILD_METADATA(Icons.Rounded.Construction),
    DONATE(Icons.Rounded.FavoriteBorder),
    TRANSLATION(Icons.Rounded.Translate),
    GENERIC()
}