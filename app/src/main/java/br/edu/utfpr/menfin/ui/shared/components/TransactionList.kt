package br.edu.utfpr.menfin.ui.shared.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun <T> TransactionList(
    modifier: Modifier = Modifier,
    items: List<T>,
    onItemPressed: (T) -> Unit,
    onLongItemPressed: (T) -> Unit = {},
    itemContent: @Composable (T) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { onLongItemPressed(item) },
                            onTap = { onItemPressed(item) }
                        )
                    }
            ) {
                itemContent(item)
            }
        }
    }
}