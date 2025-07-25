package br.edu.utfpr.menfin.ui.shared.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.edu.utfpr.menfin.R

@Composable
fun DefaultActionFormToolbar(
    modifier: Modifier = Modifier,
    isSaving: Boolean,
    onSavePressed: () -> Unit
) {
    if (isSaving) {
        CircularProgressIndicator(
            modifier = modifier
                .size(60.dp)
                .padding(all = 16.dp),
            strokeWidth = 2.dp,
            color = Color.White
        )
    } else {
        IconButton(
            modifier = modifier,
            onClick = onSavePressed
        ) {
            Icon(
                imageVector = Icons.Filled.Save,
                tint = Color.White,
                contentDescription = stringResource(R.string.generic_to_save)
            )
        }
    }
}