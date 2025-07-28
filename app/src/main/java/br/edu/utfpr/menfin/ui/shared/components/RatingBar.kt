package br.edu.utfpr.menfin.ui.shared.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.utfpr.menfin.ui.theme.MenfinTheme

@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    stars: Int = 5,
    starColor: Color = Color(0xFFFFC107)
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..stars) {
            val isSelected = i <= rating
            val icon = if (isSelected) Icons.Filled.Star else Icons.Filled.StarOutline
            val iconColor = if (isSelected) starColor else Color.Gray

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onRatingChanged(i) }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun RatingBarPreview() {
    MenfinTheme {
        RatingBar(
            rating = 3,
            onRatingChanged = {},
            stars = 5,
            starColor = Color(0xFFFFC107)
        )
    }
}