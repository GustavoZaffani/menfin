package br.edu.utfpr.menfin.ui.shared.components.form

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.ui.theme.MenfinTheme

@Composable
fun TextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onClearValue: () -> Unit,
    enabled: Boolean = true,
    @StringRes
    errorMessageCode: Int? = null,
    keyboardImeAction: ImeAction = ImeAction.Next,
    keyboardCapitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    CustomTextField(
        modifier = modifier,
        label = label,
        value = value,
        onValueChange = onValueChange,
        errorMessageCode = errorMessageCode,
        enabled = enabled,
        leadingIcon = leadingIcon,
        keyboardImeAction = keyboardImeAction,
        keyboardCapitalization = keyboardCapitalization,
        trailingIcon = {
            IconButton(onClick = onClearValue) {
                Icon(
                    imageVector = Icons.Outlined.Clear,
                    contentDescription = stringResource(R.string.generic_to_clear)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun CustomTextFieldPreview() {
    MenfinTheme {
        TextField(
            label = "Nome",
            value = "Zaffani",
            onValueChange = {},
            onClearValue = {})
    }
}