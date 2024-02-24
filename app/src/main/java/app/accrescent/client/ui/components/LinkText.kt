package app.accrescent.client.ui.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import app.accrescent.client.data.LinkTextData

@Composable
fun LinkText(
    linkTextData: LinkTextData,
    modifier: Modifier = Modifier,
) {
    val annotatedString = createAnnotatedString(linkTextData)
    val uriHandler = LocalUriHandler.current

    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium,
        onClick = { offset ->
            annotatedString.getStringAnnotations(
                start = offset,
                end = offset,
            ).firstOrNull()?.let {
                uriHandler.openUri(it.item)
            }

        },
        modifier = modifier,
    )
}

@Composable
private fun createAnnotatedString(data: LinkTextData): AnnotatedString {
    return buildAnnotatedString {
        pushStringAnnotation(
            tag = data.tag,
            annotation = data.link
        )
        withStyle(
            style = SpanStyle(
                color = Color.White,
                textDecoration = TextDecoration.Underline,
            ),
        ) {
            append(data.text)
        }
        pop()
    }

}