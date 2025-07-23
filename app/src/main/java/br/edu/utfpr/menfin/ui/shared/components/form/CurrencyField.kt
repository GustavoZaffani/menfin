package br.edu.utfpr.menfin.ui.shared.components.form

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import br.edu.utfpr.menfin.R
import br.edu.utfpr.menfin.ui.theme.MenfinTheme

@Composable
fun CurrencyField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onClearValue: () -> Unit,
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
        errorMessageCode = errorMessageCode,
        enabled = enabled,
        keyboardImeAction = keyboardImeAction,
        keyboardType = KeyboardType.Decimal,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.MonetizationOn,
                contentDescription = stringResource(R.string.generic_money)
            )

        },
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
private fun CurrencyFieldPreview() {
    MenfinTheme {
        CurrencyField(
            label = "Nome",
            value = "Zaffani",
            onValueChange = {},
            onClearValue = {})
    }
}