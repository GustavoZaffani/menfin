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
import kotlinx.coroutines.delay

@Composable
fun AnimationTypingText(
    text: String,
    modifier: Modifier = Modifier,
    typingSpeed: Long
) {
    var displayedText by remember { mutableStateOf("") }
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = text) {
        while (true) {
            displayedText = text.take(currentIndex)
            delay(typingSpeed)
            currentIndex = (currentIndex + 1) % (text.length + 1)
        }
    }

    Text(
        modifier = modifier,
        text = displayedText,
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
        )
    )
}