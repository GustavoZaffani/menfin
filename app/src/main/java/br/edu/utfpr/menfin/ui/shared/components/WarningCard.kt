package br.edu.utfpr.menfin.ui.shared.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.utfpr.menfin.ui.theme.MenfinTheme

@Composable
fun WarningCard(
    modifier: Modifier = Modifier,
    text: String
) {
    val warningContainerColor = Color(0xFFFFF3E0)
    val warningContentColor = Color(0xFFFB8C00)

    Card (
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = warningContainerColor),
        border = BorderStroke(1.dp, warningContentColor.copy(alpha = 0.5f))
    ) {
        Row (
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Aviso",
                tint = warningContentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black.copy(alpha = 0.8f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WarningCardPreview() {
    MenfinTheme {
        WarningCard(
            text = "Este lançamento teve como base um lançamento existente, portanto é necessário mudar algumas informações."
        )
    }
}