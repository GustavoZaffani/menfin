package br.edu.utfpr.menfin.ui.shared.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

fun buildAnnotatedStringWithMarkdown(text: String): AnnotatedString {
    val boldRegex = Regex("""\*\*(.*?)\*\*""")

    return buildAnnotatedString {
        var lastIndex = 0
        val matches = boldRegex.findAll(text)

        matches.forEach { match ->
            append(text.substring(lastIndex, match.range.first))

            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(match.groups[1]!!.value)
            }
            lastIndex = match.range.last + 1
        }

        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }
}
