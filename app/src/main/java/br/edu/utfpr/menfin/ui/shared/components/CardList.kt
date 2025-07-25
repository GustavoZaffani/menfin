package br.edu.utfpr.menfin.ui.shared.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun <T> CardList(
    modifier: Modifier = Modifier,
    items: List<T>,
    onItemPressed: (T) -> Unit,
    onLongItemPressed: (T) -> Unit = {},
    itemContent: @Composable (T) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        items(items) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { onLongItemPressed(item) },
                            onTap = { onItemPressed(item) }
                        )
                    },
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                itemContent(item)
            }
        }
    }
}