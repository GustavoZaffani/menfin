package br.edu.utfpr.menfin.ui.shared.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import br.edu.utfpr.menfin.ui.shared.utils.Utils.Companion.SPACE

@Composable
fun ClickableTextDefault(
    modifier: Modifier = Modifier,
    preText: String,
    clickText: String,
    onClick: () -> Unit
) {

    val annotatedText = buildAnnotatedString {
        append(preText)
        append(SPACE)
        withStyle(
            style = SpanStyle(
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            )
        ) {
            append(clickText)
        }
    }

    val startOffset = preText.length - 1

    ClickableText(
        modifier = modifier,
        text = annotatedText,
        onClick = { offset ->
            if (offset > startOffset) {
                onClick()
            }
        }
    )
}