package br.edu.utfpr.menfin.ui.shared.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import br.edu.utfpr.menfin.R

@Composable
fun ConfirmationDialog(
    modifier: Modifier = Modifier,
    title: String? = null,
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    dismissButtonText: String? = null,
    confirmButtonText: String? = null
) {
    AlertDialog(
        modifier = modifier,
        title = title?.let {
            { Text(it) }
        },
        text = { Text(text) },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmButtonText ?: stringResource(R.string.generic_to_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = dismissButtonText ?: stringResource(R.string.generic_to_cancel))
            }
        }
    )
}