package br.edu.utfpr.menfin.ui.shared.components.form

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.ui.theme.MenfinTheme

@Composable
fun PasswordField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    @StringRes
    errorMessageCode: Int? = null,
    keyboardImeAction: ImeAction = ImeAction.Next
) {
    CustomTextField(
        modifier = modifier,
        label = label,
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        errorMessageCode = errorMessageCode,
        keyboardImeAction = keyboardImeAction,
        keyboardCapitalization = KeyboardCapitalization.None,
        keyboardType = KeyboardType.Password,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = stringResource(R.string.generic_password)
            )

        },
        visualTransformation = PasswordVisualTransformation()
    )
}

@Preview(showBackground = true)
@Composable
private fun PasswordFieldPreview() {
    MenfinTheme {
        PasswordField(
            label = "Senha",
            value = "46999558659",
            onValueChange = {})
    }
}