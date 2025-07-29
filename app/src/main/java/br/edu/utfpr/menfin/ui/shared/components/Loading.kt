package br.edu.utfpr.menfin.ui.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.utfpr.menfin.ui.theme.MenfinTheme

@Composable
fun Loading(
    modifier: Modifier = Modifier,
    text: String = "Carregando"
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(72.dp)
                .padding(bottom = 20.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

        AnimationTypingText(
            text = text,
            typingSpeed = 40L,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true, heightDp = 400)
@Composable
private fun LoadingPreview() {
    MenfinTheme {
        Loading(
            text = "Carregando"
        )
    }
}