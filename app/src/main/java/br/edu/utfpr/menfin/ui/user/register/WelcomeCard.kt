package br.edu.utfpr.menfin.ui.user.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.ui.theme.MenfinTheme

@Composable
fun WelcomeCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    user: String = ""
) {
    val welcomeText =
        if (user.isNotBlank()) "${stringResource(R.string.generic_welcome)}, $user!" else "${
            stringResource(R.string.generic_welcome)
        }!"

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp)
            )
            Column {
                Text(
                    text = welcomeText,
                    fontSize = 20.sp,
                    color = Color.White
                )
                Text(
                    text = stringResource(R.string.welcome_card_make_register_description),
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun WelcomeCardPreview() {
    MenfinTheme {
        WelcomeCard(
            icon = Icons.Filled.People,
            user = "Juvenal"
        )
    }
}