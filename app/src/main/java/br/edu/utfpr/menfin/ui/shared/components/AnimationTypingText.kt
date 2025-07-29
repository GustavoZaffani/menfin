package br.edu.utfpr.menfin.ui.shared.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay

@Composable
fun AnimationTypingText(
    text: String,
    modifier: Modifier = Modifier,
    typingSpeed: Long = 50L,
    showEllipsis: Boolean = true
) {
    var displayedText by remember { mutableStateOf("") }
    var showDots by remember { mutableStateOf(false) }

    LaunchedEffect(text) {
        displayedText = ""
        for (i in text.indices) {
            displayedText += text[i]
            delay(typingSpeed)
        }
        if (showEllipsis) {
            while (true) {
                showDots = true
                delay(500)
                showDots = false
                delay(500)
            }
        }
    }

    Text(
        modifier = modifier,
        text = displayedText + if (showEllipsis && showDots) "..." else "",
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    )
}